package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.Tessellator;

import org.fentanylsolutions.tabfaces.access.IMixinGuiMultiplayer;
import org.fentanylsolutions.tabfaces.access.IMixinServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.authlib.GameProfile;

@SuppressWarnings("unused")
@Mixin(ServerListEntryNormal.class)
public class MixinServerListEntryNormal {

    @Shadow
    @Final
    GuiMultiplayer field_148303_c;

    @Shadow
    @Final
    ServerData field_148301_e;

    @Inject(method = "drawEntry", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onDrawEntryTail(int index, int x, int y, int width, int height, Tessellator tessellator, int mouseX,
        int mouseY, boolean isSelected, CallbackInfo ci, boolean flag1, boolean flag2, boolean flag3, List<?> list,
        String s2, int i2, byte b0, String playerListString, int j2, String ping, int k2, int l2, String tooltip) {
        GameProfile[] profiles = ((IMixinServerData) field_148301_e).getProfiles();
        if (tooltip != null) {
            // pass, forge tooltip
        } else if (k2 >= width - 15 && k2 <= width - 5 && l2 >= 0 && l2 <= 8) {
            // pass, ping tooltip
        } else if (k2 >= width - i2 - 15 - 2 && k2 <= width - 15 - 2 && l2 >= 0 && l2 <= 8) {
            /* If we're here that means we are drawing this ServerListEntry's player tooltip */
            ((IMixinGuiMultiplayer) field_148303_c).setVisiblePlayers(profiles);
        }
    }
}
