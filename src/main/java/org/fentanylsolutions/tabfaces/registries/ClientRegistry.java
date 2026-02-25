package org.fentanylsolutions.tabfaces.registries;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.compat.LoadedMods;
import org.fentanylsolutions.tabfaces.compat.skinport.SkinPortCompat;
import org.fentanylsolutions.tabfaces.util.MarkerCallback;
import org.fentanylsolutions.tabfaces.util.PingUtil;
import org.fentanylsolutions.tabfaces.util.Util;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

public class ClientRegistry {

    public enum FetchState {
        PENDING_UUID,
        PENDING_PROFILE,
        RESOLVING_PROFILE,
        PENDING_SKIN,
        RESOLVED,
        RESOLVED_PLACEHOLDER,
        FAILED
    }

    private static final int MAX_RETRIES = 3;
    private static final long[] BACKOFF_MS = { 2000, 8000, 30000 };

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
        2,
        4,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(64),
        new java.util.concurrent.ThreadFactory() {

            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "TabFaces-Worker-" + counter.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        },
        new ThreadPoolExecutor.DiscardOldestPolicy());

    private final Map<String, Data> playerEntities;
    private float tickCounter;
    private final AtomicBoolean pingInFlight = new AtomicBoolean(false);
    private volatile long lastPingTimestamp = 0;

    public ClientRegistry() {
        this.playerEntities = new ConcurrentHashMap<>();
        this.tickCounter = 0;
    }

    public boolean displayNameInRegistry(String displayName) {
        return playerEntities.containsKey(displayName);
    }

    public Data getByDisplayName(String displayName) {
        return playerEntities.get(displayName);
    }

    public void insert(String displayName, UUID id, ResourceLocation skinResourceLocation, boolean removeAfterTTL,
        int ttl) {

        TabFaces.debug(
            "Inserted " + displayName
                + ", "
                + (id != null ? id.toString() : "null")
                + ", "
                + (skinResourceLocation != null ? skinResourceLocation.toString() : "null"));

        Data existing = playerEntities.get(displayName);
        if (existing != null) {
            if (id != null && existing.id == null) {
                existing.id = id;
            }
            if (skinResourceLocation != null && existing.skinResourceLocation == null) {
                existing.skinResourceLocation = skinResourceLocation;
            }
            return;
        }

        Data newData = new Data(displayName, id, skinResourceLocation, removeAfterTTL, ttl);
        if (id != null) {
            newData.state = FetchState.PENDING_PROFILE;
        } else if (skinResourceLocation != null) {
            newData.state = FetchState.RESOLVED_PLACEHOLDER;
        } else {
            newData.state = FetchState.PENDING_UUID;
        }
        playerEntities.put(displayName, newData);
    }

    public void insertOrUpdateFromPing(String displayName, UUID id, boolean removeAfterTTL, int ttl) {
        Data existing = playerEntities.get(displayName);
        if (existing != null) {
            if (existing.id == null && id != null) {
                existing.id = id;
                if (existing.state == FetchState.PENDING_UUID || existing.state == FetchState.RESOLVED_PLACEHOLDER) {
                    existing.state = FetchState.PENDING_PROFILE;
                    existing.retryCount = 0;
                }
                TabFaces.debug("Updated " + displayName + " with UUID " + id);
            }
        } else {
            Data data = new Data(displayName, id, getDefaultSkin(displayName), removeAfterTTL, ttl);
            data.state = (id != null) ? FetchState.PENDING_PROFILE : FetchState.RESOLVED_PLACEHOLDER;
            playerEntities.put(displayName, data);
            TabFaces.debug("Inserted " + displayName + " from ping with UUID " + (id != null ? id : "null"));
        }
    }

    public void removeByDisplayName(String displayName) {
        TabFaces.debug("Removed " + displayName + " by displayname");
        playerEntities.remove(displayName);
    }

    private ResourceLocation getDefaultSkin(String displayName) {
        return Config.showQuestionMarkIfUnknown ? TabFaces.varInstanceClient.defaultResourceLocation
            : AbstractClientPlayer.locationStevePng;
    }

    public ResourceLocation getTabMenuResourceLocation(String displayName, boolean removeAfterTTL, int ttl) {
        /* thePlayer is null when we're in the server selection menu */
        if (VarInstanceClient.minecraftRef.thePlayer != null
            && VarInstanceClient.minecraftRef.thePlayer.getDisplayName()
                .equals(displayName)) {
            return VarInstanceClient.minecraftRef.thePlayer.getLocationSkin();
        }

        Data data = playerEntities.get(displayName);

        if (data == null) {
            Data newData = new Data(displayName, null, getDefaultSkin(displayName), removeAfterTTL, ttl);
            newData.state = FetchState.PENDING_UUID;
            Data existing = playerEntities.putIfAbsent(displayName, newData);
            if (existing != null) {
                data = existing;
            } else {
                data = newData;
                submitPingTask(displayName);
            }
            return data.skinResourceLocation != null ? data.skinResourceLocation : getDefaultSkin(displayName);
        }

        switch (data.state) {
            case PENDING_UUID:
                maybeSubmitPingTask(displayName);
                break;

            case PENDING_PROFILE:
                if (data.id != null && shouldRetry(data)) {
                    data.state = FetchState.RESOLVING_PROFILE;
                    data.lastAttemptTimestamp = System.currentTimeMillis();
                    submitProfileResolutionTask(data);
                }
                break;

            case RESOLVING_PROFILE:
                break;

            case PENDING_SKIN:
                resolveSkinFromManager(data);
                break;

            case RESOLVED:
                break;

            case RESOLVED_PLACEHOLDER:
            case FAILED:
                if (data.id != null
                    && System.currentTimeMillis() - data.lastAttemptTimestamp >= Config.failedRetrySeconds * 1000L) {
                    TabFaces.debug(
                        "Recycling " + data.state
                            + " entry for "
                            + data.displayName
                            + " after "
                            + Config.failedRetrySeconds
                            + "s");
                    data.profile = null;
                    data.skinResourceLocation = null;
                    data.retryCount = 0;
                    data.state = FetchState.PENDING_PROFILE;
                }
                break;
        }

        if (LoadedMods.skinPortLoaded && data.id != null) {
            return SkinPortCompat.getSkinPortCachedSkin(data.id, displayName);
        }

        return data.skinResourceLocation != null ? data.skinResourceLocation : getDefaultSkin(displayName);
    }

    private void resolveSkinFromManager(Data data) {
        try {
            SkinManager skinManager = Minecraft.getMinecraft()
                .func_152342_ad();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = skinManager
                .func_152788_a(data.profile);

            if (textures != null && textures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                MinecraftProfileTexture texture = textures.get(MinecraftProfileTexture.Type.SKIN);
                ResourceLocation rl = skinManager
                    .func_152789_a(texture, MinecraftProfileTexture.Type.SKIN, new MarkerCallback());
                data.skinResourceLocation = rl;
                data.state = FetchState.RESOLVED;
                TabFaces.debug("Skin fully resolved for " + data.displayName + ": " + rl);
            } else {
                skinManager.func_152790_a(data.profile, new MarkerCallback(), true);
            }
        } catch (Exception e) {
            TabFaces.error("Error resolving skin for " + data.displayName + ": " + e.getMessage());
        }
    }

    private void submitPingTask(String targetDisplayName) {
        if (!pingInFlight.compareAndSet(false, true)) {
            return;
        }
        executor.submit(() -> {
            try {
                lastPingTimestamp = System.currentTimeMillis();
                TabFaces.debug("Starting ping task for " + targetDisplayName);
                PingUtil.ServerStatusCallbackClientRegistry callback = new PingUtil.ServerStatusCallbackClientRegistry();
                PingUtil.pingServer(callback, targetDisplayName);
                TabFaces.debug("Ping task completed for " + targetDisplayName);
            } catch (Exception e) {
                TabFaces.error("Server ping failed: " + e.getMessage());
            } finally {
                pingInFlight.set(false);
            }
        });
    }

    private void maybeSubmitPingTask(String displayName) {
        if (System.currentTimeMillis() - lastPingTimestamp > 5000) {
            submitPingTask(displayName);
        }
    }

    private void submitProfileResolutionTask(Data data) {
        executor.submit(() -> {
            try {
                TabFaces.debug("Resolving profile for " + data.displayName);
                GameProfile profile = Util.getFullProfile(data.id, data.displayName);

                if (profile != null && !profile.getProperties()
                    .isEmpty()) {
                    data.profile = profile;
                    data.state = FetchState.PENDING_SKIN;
                    TabFaces.debug("Profile resolved for " + data.displayName + " with properties");
                } else if (profile != null) {
                    data.profile = profile;
                    data.skinResourceLocation = getDefaultSkin(data.displayName);
                    data.lastAttemptTimestamp = System.currentTimeMillis();
                    data.state = FetchState.RESOLVED_PLACEHOLDER;
                    TabFaces.debug("Profile resolved for " + data.displayName + " but no skin properties");
                } else {
                    handleProfileResolutionFailure(data);
                }
            } catch (Exception e) {
                TabFaces.error("Profile resolution exception for " + data.displayName + ": " + e.getMessage());
                handleProfileResolutionFailure(data);
            }
        });
    }

    private void handleProfileResolutionFailure(Data data) {
        data.retryCount++;
        if (data.retryCount < MAX_RETRIES) {
            data.lastAttemptTimestamp = System.currentTimeMillis();
            data.state = FetchState.PENDING_PROFILE;
            TabFaces.debug(
                "Profile resolution failed for " + data.displayName
                    + ", will retry (attempt "
                    + data.retryCount
                    + "/"
                    + MAX_RETRIES
                    + ")");
        } else {
            data.skinResourceLocation = getDefaultSkin(data.displayName);
            data.lastAttemptTimestamp = System.currentTimeMillis();
            data.state = FetchState.FAILED;
            TabFaces.warn(
                "Profile resolution permanently failed for " + data.displayName
                    + " after "
                    + MAX_RETRIES
                    + " attempts");
        }
    }

    private boolean shouldRetry(Data data) {
        if (data.retryCount == 0) return true;
        if (data.retryCount >= MAX_RETRIES) return false;
        long elapsed = System.currentTimeMillis() - data.lastAttemptTimestamp;
        return elapsed >= BACKOFF_MS[Math.min(data.retryCount - 1, BACKOFF_MS.length - 1)];
    }

    public void clear() {
        TabFaces.debug("Clearing registry");
        playerEntities.clear();
    }

    public void tick() {
        this.tickCounter++;

        if (this.tickCounter / 20.0 >= Config.skinTtlInterval) {
            this.tickCounter = 0;
            TabFaces.debug("Running skin TTL check (every " + Config.skinTtlInterval + " seconds)");

            long currentTime = System.currentTimeMillis();
            Iterator<Map.Entry<String, Data>> iterator = playerEntities.entrySet()
                .iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, Data> entry = iterator.next();
                Data data = entry.getValue();

                long elapsedSeconds = (currentTime - data.timestamp) / 1000;

                if (elapsedSeconds > data.ttl) {
                    if (data.removeAfterTTL) {
                        TabFaces.debug("TTL expired and removeAfterTTL is true, removing entry");
                        iterator.remove();
                    } else if (data.skinResourceLocation != null) {
                        TabFaces.debug("TTL expired, resetting to PENDING_SKIN for re-fetch");
                        data.skinResourceLocation = null;
                        data.retryCount = 0;
                        if (data.profile != null) {
                            data.state = FetchState.PENDING_SKIN;
                        } else if (data.id != null) {
                            data.state = FetchState.PENDING_PROFILE;
                        } else {
                            data.state = FetchState.PENDING_UUID;
                        }
                    }
                }
            }
        }
    }

    public class Data {

        String displayName;
        public volatile UUID id;
        public volatile ResourceLocation skinResourceLocation;
        volatile GameProfile profile;
        volatile FetchState state;
        volatile long lastAttemptTimestamp;
        volatile int retryCount;
        long timestamp;
        boolean removeAfterTTL;
        int ttl;

        Data(String displayName, UUID id, ResourceLocation skinResourceLocation, boolean removeAfterTTL, int ttl) {
            this.displayName = displayName;
            this.id = id;
            this.profile = null;
            this.skinResourceLocation = skinResourceLocation;
            this.state = FetchState.PENDING_UUID;
            this.lastAttemptTimestamp = 0;
            this.retryCount = 0;
            this.timestamp = System.currentTimeMillis();
            this.removeAfterTTL = removeAfterTTL;
            if (ttl == -1) {
                this.ttl = Config.skinTtl;
            } else {
                this.ttl = ttl;
            }
        }

        public boolean hasRealSkin() {
            return state == FetchState.RESOLVED;
        }
    }
}
