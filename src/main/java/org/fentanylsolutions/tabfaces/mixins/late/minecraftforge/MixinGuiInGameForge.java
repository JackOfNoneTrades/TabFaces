package org.fentanylsolutions.tabfaces.mixins.late.minecraftforge;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiIngameForge.class)
public class MixinGuiInGameForge {

    @Redirect(
        method = "renderPlayerList",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I"))
    private int shiftPlayerNameX(FontRenderer fontRenderer, String text, int x, int y, int color) {
        return fontRenderer.drawStringWithShadow(text, x + 10, y, color);
    }

    @SuppressWarnings("unused")
    @Inject(
        method = "renderPlayerList",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I",
            ordinal = 0),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void onInjectAfterRedirect(int width, int height, CallbackInfo ci, ScoreObjective scoreobjective,
        NetHandlerPlayClient handler, List<GuiPlayerInfo> players, int maxPlayers, int rows, int columns,
        int columnWidth, int left, byte border, int i, int xPos, int yPos, GuiPlayerInfo player, ScorePlayerTeam team,
        String displayName) {
        ResourceLocation rl = TabFaces.varInstanceClient.clientRegistry
            .getTabMenuResourceLocation(player.name, false, -1);
        if (rl != null) {
            VarInstanceClient.minecraftRef.getTextureManager()
                .bindTexture(rl);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            // int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float
            // tileWidth, float tileHeight
            Gui.func_152125_a(xPos, yPos, 8, 14, 8, 18, 8, 8, 64.0F, 64.0F);

        }
    }
}
