package org.fentanylsolutions.tabfaces.mixins.late.simpleskinbackport;

import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;

import org.fentanylsolutions.tabfaces.util.ISkinCallbackMarker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import roadhog360.simpleskinbackport.core.Utils;

@Mixin(value = Utils.class, remap = false)
public class MixinUtils {

    /**
     * @author jack
     * @reason Extend isPlayer check to allow custom skin callbacks
     */
    @Overwrite
    public static boolean isPlayer(MinecraftProfileTexture.Type type, SkinManager.SkinAvailableCallback callback) {
        return type == MinecraftProfileTexture.Type.SKIN
            && (callback instanceof EntityPlayer || callback instanceof ISkinCallbackMarker);
    }
}
