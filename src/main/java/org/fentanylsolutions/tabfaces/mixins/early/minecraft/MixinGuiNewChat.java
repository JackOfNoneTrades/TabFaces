package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.util.ClientUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

@SuppressWarnings("unused")
@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {

    ChatLine currentChatLine;
    int currentI2;

    @Inject(
        method = "drawChat(I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I"))
    private void captureChatLine(CallbackInfo ci, @Local ChatLine chatLine, @Local(ordinal = 8) int i2) {
        currentChatLine = chatLine;
        currentI2 = i2;
    }

    @Redirect(
        method = "drawChat(I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I"))
    private int redirectDrawStringWithShadow(FontRenderer fontRenderer, String text, int x, int y, int color) {

        if (Config.enableFacesInChat && currentChatLine != null) {
            if (currentChatLine.func_151461_a()
                .getSiblings()
                .size() > 2) {
                if (currentChatLine.func_151461_a()
                    .getSiblings()
                    .get(0)
                    .getUnformattedText()
                    .equals("<")
                    && currentChatLine.func_151461_a()
                        .getSiblings()
                        .get(2)
                        .getUnformattedText()
                        .equals("> ")) {
                    ResourceLocation rl = TabFaces.varInstanceClient.clientRegistry.getTabMenuResourceLocation(
                        currentChatLine.func_151461_a()
                            .getSiblings()
                            .get(1)
                            .getUnformattedText(),
                        false,
                        -1);
                    if (rl != null) {
                        float alpha = (float) (color >> 24 & 255) / 255.0F;
                        ClientUtil.drawPlayerFace(rl, x + 1, y - 0.5f, alpha);
                        x += 11;
                    }
                }
            }
        }

        return fontRenderer.drawStringWithShadow(text, x, y, color);
    }
}
