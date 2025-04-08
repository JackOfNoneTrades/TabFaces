package org.fentanylsolutions.tabfaces.registries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

import com.mojang.authlib.GameProfile;
import org.fentanylsolutions.tabfaces.TabFaces;

public class ClientRegistry {

    private Map<String, Data> playerEntities;

    public ClientRegistry() {
        this.playerEntities = new HashMap<>();
    }

    public void insert(String displayName, UUID id, ResourceLocation skinResourceLocation) {
        playerEntities.putIfAbsent(displayName, new Data(displayName, id, skinResourceLocation));
    }

    public void removeByDisplayName(String displayName) {
        playerEntities.remove(displayName);
    }

    public void setTabMenuResourceLocation(String displayName, UUID id, ResourceLocation tabMenuResourceLocation) {
        Data data = playerEntities.get(displayName);
        if (data == null) {
            insert(displayName, id, tabMenuResourceLocation);
        } else {
            data.tabMenuResourceLocation = tabMenuResourceLocation;
        }
    }

    public ResourceLocation getTabMenuResourceLocation(String displayName) {
        if (TabFaces.minecraftRef.thePlayer.getDisplayName()
            .equals(displayName)) {
            return TabFaces.minecraftRef.thePlayer.getLocationSkin();
        }
        Data data = playerEntities.get(displayName);
        GameProfile gameprofile = new GameProfile(null, displayName);
        TabFaces.sessionService.fillProfileProperties(gameprofile, true);
        return (data != null) ? data.tabMenuResourceLocation : null;
    }

    public void clear() {
        playerEntities.clear();
    }

    private class Data {

        String displayName;
        UUID id;
        ResourceLocation tabMenuResourceLocation;

        Data(String displayName, UUID id, ResourceLocation tabResourceLocation) {
            this.displayName = displayName;
            this.tabMenuResourceLocation = tabResourceLocation;
        }
    }
}
