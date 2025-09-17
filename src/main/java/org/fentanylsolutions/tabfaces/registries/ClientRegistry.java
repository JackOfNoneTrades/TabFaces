package org.fentanylsolutions.tabfaces.registries;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.compat.LoadedMods;
import org.fentanylsolutions.tabfaces.compat.skinport.SkinPortCompat;
import org.fentanylsolutions.tabfaces.util.ClientUtil;
import org.fentanylsolutions.tabfaces.util.MarkerCallback;
import org.fentanylsolutions.tabfaces.util.PingUtil;
import org.fentanylsolutions.tabfaces.util.Util;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

public class ClientRegistry {

    private Map<String, Data> playerEntities;
    private float tickCounter;
    private final AtomicBoolean fetchingServerStatus = new AtomicBoolean(false);

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

        if (id != null) {
            TabFaces.debug(
                "Inserted " + displayName
                    + ", "
                    + id
                    + ", "
                    + (skinResourceLocation != null ? skinResourceLocation.toString() : "null"));
        } else {
            TabFaces.debug(
                "Inserted " + displayName
                    + ", "
                    + "null"
                    + ", "
                    + (skinResourceLocation != null ? skinResourceLocation.toString() : "null"));
        }

        synchronized (this) {
            Data existing = playerEntities.get(displayName);
            boolean foundSkin = existing != null && existing.foundRealSkin;

            Data newData = new Data(displayName, id, skinResourceLocation, removeAfterTTL, ttl);
            newData.foundRealSkin = foundSkin;

            playerEntities.put(displayName, newData);
        }
    }

    public void removeByDisplayName(String displayName) {
        TabFaces.debug("Removed " + displayName + " by displayname");
        playerEntities.remove(displayName);
    }

    public static ResourceLocation getCachedRL(UUID id, String displayname) {
        GameProfile profile = new GameProfile(id, displayname);
        SkinManager skinManager = Minecraft.getMinecraft()
            .func_152342_ad();

        GameProfile fullProfile = VarInstanceClient.sessionService.fillProfileProperties(profile, false);
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = skinManager.func_152788_a(fullProfile);

        if (textures != null && textures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            MinecraftProfileTexture texture = textures.get(MinecraftProfileTexture.Type.SKIN);
            return skinManager.func_152789_a(texture, MinecraftProfileTexture.Type.SKIN, new MarkerCallback());
        } else {
            skinManager.func_152790_a(fullProfile, null, true); // Callback = null, we don't care
            return SkinManager.field_152793_a;
        }
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
            if (fetchingServerStatus.compareAndSet(false, true)) {
                new Thread(() -> {
                    TabFaces.debug("Starting new ServerPingThread");
                    PingUtil.ServerStatusCallbackClientRegistry callback = new PingUtil.ServerStatusCallbackClientRegistry();
                    PingUtil.pingServer(callback, displayName);
                    fetchingServerStatus.set(false);
                    TabFaces.debug("ServerPingThread Done");
                }, "ServerPingThread-" + displayName).start();
            }
            return Config.showQuestionMarkIfUnknown ? TabFaces.varInstanceClient.defaultResourceLocation
                : AbstractClientPlayer.locationStevePng;
        }
        synchronized (data) {
            if (data.skinResourceLocation == null && data.profile == null && !data.resolving) {
                data.resolving = true;
                new Thread(() -> {
                    TabFaces.debug("Starting new GameProfileResolverThread");
                    GameProfile profile = Util.getFullProfile(data.id, data.displayName);
                    TabFaces.debug("Got full profile.");

                    synchronized (data) {
                        data.profile = profile;
                        if (!data.profile.getProperties()
                            .isEmpty()) {
                            data.foundRealSkin = true;
                            TabFaces.debug("Properties of " + data.displayName + " are not empty.");
                        }
                        data.resolving = false;
                    }
                }, "GameProfileResolverThread-" + displayName).start();
            } else if (data.skinResourceLocation == null && data.profile != null && !data.resolving) {
                insert(displayName, data.id, ClientUtil.skinResourceLocation(data.profile), removeAfterTTL, ttl);
            }
        }
        if (LoadedMods.skinPortLoaded) {
            return SkinPortCompat.getSkinPortCachedSkin(data.id, displayName);
        } else {
            return getCachedRL(data.id, displayName); // data.skinResourceLocation;
        }
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
                    synchronized (data) {
                        if (data.removeAfterTTL) {
                            TabFaces.debug("TTL expired and removeAfterTTL is true, removing entry");
                            iterator.remove();
                        } else if (data.skinResourceLocation != null) {
                            TabFaces.debug("TTL expired, clearing skinResourceLocation");
                            data.skinResourceLocation = null;
                        }
                    }
                }
            }
        }
    }

    public class Data {

        String displayName;
        public UUID id;
        public ResourceLocation skinResourceLocation;
        volatile boolean resolving = false;
        volatile GameProfile profile;
        long timestamp;
        boolean removeAfterTTL;
        int ttl;
        public volatile boolean foundRealSkin;

        Data(String displayName, UUID id, ResourceLocation skinResourceLocation, boolean removeAfterTTL, int ttl) {
            this.displayName = displayName;
            this.id = id;
            this.profile = null;
            this.skinResourceLocation = skinResourceLocation;
            this.timestamp = System.currentTimeMillis();
            this.removeAfterTTL = removeAfterTTL;
            this.foundRealSkin = false;
            if (ttl == -1) {
                this.ttl = Config.skinTtl;
            } else {
                this.ttl = ttl;
            }
        }
    }
}
