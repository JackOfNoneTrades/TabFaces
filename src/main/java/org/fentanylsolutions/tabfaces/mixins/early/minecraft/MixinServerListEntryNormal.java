package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.access.IMixinGuiMultiplayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(ServerListEntryNormal.class)
public class MixinServerListEntryNormal {

    @Shadow
    GuiMultiplayer field_148303_c;

    @Inject(method = "drawEntry", at = @At("TAIL"))
    private void onDrawEntryTail(int index, int x, int y, int width, int height,
        net.minecraft.client.renderer.Tessellator tessellator, int mouseX, int mouseY, boolean isSelected,
        CallbackInfo ci) {
        ((IMixinGuiMultiplayer) field_148303_c).setVisiblePlayers(TabFaces.varInstanceClient.gameProfiles);
    }
}
