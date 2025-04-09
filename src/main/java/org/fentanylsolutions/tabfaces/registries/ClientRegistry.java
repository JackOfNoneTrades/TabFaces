package org.fentanylsolutions.tabfaces.registries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.util.Util;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;

import com.mojang.authlib.GameProfile;

public class ClientRegistry {

    private Map<String, Data> playerEntities;
    private float tickCounter;

    public ClientRegistry() {
        this.playerEntities = new HashMap<>();
        this.tickCounter = 0;
    }

    public void insert(String displayName, UUID id, ResourceLocation skinResourceLocation) {
        TabFaces.debug(
            "Inserted " + displayName
                + ", "
                + id.toString()
                + ", "
                + (skinResourceLocation != null ? skinResourceLocation.toString() : "null"));
        playerEntities.put(displayName, new Data(displayName, id, skinResourceLocation));
    }

    public void removeByDisplayName(String displayName) {
        TabFaces.debug("Removed " + displayName + " by displayname");
        playerEntities.remove(displayName);
    }

    public ResourceLocation getTabMenuResourceLocation(String displayName) {
        if (VarInstanceClient.minecraftRef.thePlayer.getDisplayName()
            .equals(displayName)) {
            return VarInstanceClient.minecraftRef.thePlayer.getLocationSkin();
        }
        Data data = playerEntities.get(displayName);
        if (data == null) {
            return Config.showQuestionMarkIfUnknown ? TabFaces.varInstanceClient.defaultResourceLocation
                : AbstractClientPlayer.locationStevePng;
        }
        if (data.skinResourceLocation == null && data.profile == null && !data.resolving) {
            data.resolving = true;
            new Thread(() -> {
                data.profile = Util.getFullProfile(data.id, data.displayName);
                data.resolving = false;
            }, "GameProfileResolverThread-" + displayName).start();
        } else if (data.skinResourceLocation == null && data.profile != null && !data.resolving) {
            insert(displayName, data.id, Util.skinResourceLocation(data.profile));
        }
        return data.skinResourceLocation;
    }

    public void clear() {
        TabFaces.debug("Clearing registry");
        playerEntities.clear();
    }

    public void tick() {
        if (!Util.onServer()) {
            return;
        }
        this.tickCounter++;
        if (this.tickCounter / 20.0 >= Config.skinTtlInterval) {
            this.tickCounter = 0;
            TabFaces.debug("Running skin TTL check (every " + Config.skinTtlInterval + " seconds)");
            long currentTime = System.currentTimeMillis();
            for (Data data : playerEntities.values()) {
                if (data.skinResourceLocation != null && (currentTime - data.timestamp > Config.skinTtl * 1000)) {
                    TabFaces.debug("Skin too old, setting to null");
                    data.skinResourceLocation = null;
                }
            }
        }
    }

    private class Data {

        String displayName;
        UUID id;
        ResourceLocation skinResourceLocation;
        volatile boolean resolving = false;
        volatile GameProfile profile;
        long timestamp;

        Data(String displayName, UUID id, ResourceLocation skinResourceLocation) {
            this.displayName = displayName;
            this.id = id;
            this.profile = null;
            this.skinResourceLocation = skinResourceLocation;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
