package org.fentanylsolutions.tabfaces.mixins.mid.minecraftforge;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraftforge.client.GuiIngameForge;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.util.ClientUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.sugar.Local;

@SuppressWarnings("unused")
@Mixin(GuiIngameForge.class)
public class MixinGuiInGameForge {

    @Redirect(
        method = "renderPlayerList",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I"))
    private int shiftPlayerNameX(FontRenderer fontRenderer, String text, int x, int y, int color) {
        return fontRenderer.drawStringWithShadow(text, x + (Config.enableFacesInTabMenu ? 10 : 0), y, color);
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
        if (!Config.enableFacesInTabMenu) {
            return;
        }
        ClientUtil.drawPlayerFace(player.name, xPos, yPos, 1.0f);
    }

    @ModifyVariable(remap = false, method = "renderPlayerList", at = @At("STORE"), ordinal = 2)
    private int injectedMaxPlayers(int maxPlayers, @Local NetHandlerPlayClient handler) {
        /* Adding fake players for debugging */
        /*
         * List<net.minecraft.client.gui.GuiPlayerInfo> newPlayerInfoList = new ArrayList();
         * for (int i = 0; i < 21; i++) {
         * newPlayerInfoList.add(handler.playerInfoList.get(0));
         * }
         * handler.playerInfoList = newPlayerInfoList;
         */
        if (Config.trimTabMenu) {
            return handler.playerInfoList.size();
        } else {
            return maxPlayers;
        }
    }

}
