package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import java.util.List;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;

import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.access.IMixinGuiMultiplayer;
import org.fentanylsolutions.tabfaces.access.IMixinServerData;
import org.fentanylsolutions.tabfaces.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends GuiScreen implements IMixinGuiMultiplayer {
    public GameProfile[] visibleInfo;
    //@Shadow
    //ServerData field_146811_z;

    @Shadow
    ServerSelectionList field_146803_h;

    /**
     * Redirects the call to func_146283_a inside drawScreen to call our own method instead.
     */
    @Redirect(
        method = "drawScreen",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiMultiplayer;func_146283_a(Ljava/util/List;II)V"))
    private void redirectTooltipRendering(GuiMultiplayer instance, List<String> textLines, int x, int y) {
        // Replace this with your own logic
        myCustomTooltipRenderer(textLines, x, y);
    }

    @Override
    public void setVisiblePlayers(GameProfile[] players) {
        visibleInfo = players;
    }

    private void myCustomTooltipRenderer(List<String> lines, int mouseX, int mouseY) {
        //TabFaces.error("SNEED: " + mouseX + ":" + mouseY);
        //System.out.println(field_146811_z);
        //System.out.println(this.visibleInfo);
        Util.drawHoveringTextWithFaces(this, visibleInfo, lines, mouseX, mouseY);
    }
}
