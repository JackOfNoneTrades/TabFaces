package org.fentanylsolutions.tabfaces.util;

import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

public class MarkerCallback implements SkinManager.SkinAvailableCallback, ISkinCallbackMarker {

    @Override
    public void func_152121_a(MinecraftProfileTexture.Type skinPart, ResourceLocation skinLoc) {
        // no-op
    }
}
