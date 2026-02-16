package org.fentanylsolutions.tabfaces.util;

import java.util.UUID;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.compat.LoadedMods;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class Util {

    public static boolean isServer() {
        return FMLCommonHandler.instance()
            .getSide() == Side.SERVER;
    }

    /* This function should be called from a thread, as it makes a sync network call */
    public static GameProfile getFullProfile(UUID id, String displayName) {
        if (id == null) {
            TabFaces.warn("Cannot resolve profile for " + displayName + ": UUID is null");
            return null;
        }
        GameProfile profile = new GameProfile(id, displayName);
        try {
            return VarInstanceClient.sessionService.fillProfileProperties(profile, true);
        } catch (Exception e) {
            TabFaces.error("Failed to get profile for " + displayName + ":" + id.toString());
        }
        return null;
    }

    public static boolean useNewSkinFormat() {
        return LoadedMods.skinPortLoaded || LoadedMods.simpleSkinBackportLoaded || Config.forceNewSkinCompat;
    }
}
