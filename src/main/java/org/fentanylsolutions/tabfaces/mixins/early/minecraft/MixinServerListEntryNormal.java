package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;

import org.fentanylsolutions.tabfaces.access.IMixinGuiMultiplayer;
import org.fentanylsolutions.tabfaces.access.IMixinServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerListEntryNormal.class)
public class MixinServerListEntryNormal {

    @Shadow
    GuiMultiplayer field_148303_c;

    @Shadow
    ServerData field_148301_e;

    @Inject(method = "drawEntry", at = @At("TAIL"))
    private void onDrawEntryTail(int index, int x, int y, int width, int height,
        net.minecraft.client.renderer.Tessellator tessellator, int mouseX, int mouseY, boolean isSelected,
        CallbackInfo ci) {
        // Your code here
        // System.out.println("Injected at the end of drawEntry for index: " + index);
        ((IMixinGuiMultiplayer) field_148303_c)
            .setVisiblePlayers(((IMixinServerData) field_148301_e).getVisiblePlayers());
    }
}
