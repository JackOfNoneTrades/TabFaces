package org.fentanylsolutions.tabfaces.util;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class Util {

    public static boolean isServer() {
        return FMLCommonHandler.instance()
            .getSide() == Side.SERVER;
    }

    public static ResourceLocation skinResourceLocation(GameProfile profile) {
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> resultMap = VarInstanceClient.minecraftRef
            .func_152342_ad()
            .func_152788_a(profile);
        if (resultMap.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            ResourceLocation location = VarInstanceClient.minecraftRef.func_152342_ad()
                .func_152792_a(resultMap.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            TabFaces.debug(location.toString());
            return location;
        }
        return Config.showQuestionMarkIfUnknown ? TabFaces.varInstanceClient.defaultResourceLocation
            : AbstractClientPlayer.locationStevePng;
    }

    /* This function should be called from a thread, as it makes a sync network call */
    public static GameProfile getFullProfile(UUID id, String displayName) {
        GameProfile profile = new GameProfile(id, displayName);
        try {
            return VarInstanceClient.sessionService.fillProfileProperties(profile, true);
        } catch (Exception e) {
            TabFaces.error("Failed to get profile for " + displayName + ":" + id.toString());
        }
        return null;
    }

    public static boolean onServer() {
        Minecraft mc = Minecraft.getMinecraft();
        return !mc.isSingleplayer() && !mc.isIntegratedServerRunning() && mc.thePlayer != null;
    }
}
