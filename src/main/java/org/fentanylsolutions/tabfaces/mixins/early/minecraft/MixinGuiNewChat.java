package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
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
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

@SuppressWarnings("unused")
@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {

    @Inject(
        method = "drawChat(I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I"))
    private void captureChatLine(CallbackInfo ci, @Local ChatLine chatLine,
        @Share("chatLine") LocalRef<ChatLine> chatLineRef) {
        chatLineRef.set(chatLine);
    }

    private int drawFaceAndText(FontRenderer fontRenderer, int x, int y, int color, List<IChatComponent> siblings,
        int start, int nameIndex, int end) {
        StringBuilder prefix = new StringBuilder();
        for (int j = 0; j < start; ++j) {
            prefix.append(
                siblings.get(j)
                    .getFormattedText());
        }

        StringBuilder nameAndRest = new StringBuilder();
        for (int j = start; j < siblings.size(); ++j) {
            nameAndRest.append(
                siblings.get(j)
                    .getFormattedText());
        }

        int xCursor = x;
        if (prefix.length() > 0) {
            xCursor += fontRenderer.drawStringWithShadow(prefix.toString(), xCursor, y, color) - 1;
        }

        String rawName = siblings.get(nameIndex)
            .getUnformattedText()
            .replaceAll("(?i)§[0-9A-FK-OR]", "");

        ResourceLocation rl = TabFaces.varInstanceClient.clientRegistry.getTabMenuResourceLocation(rawName, false, -1);

        if (rl != null) {
            float alpha = (float) (color >> 24 & 255) / 255.0F;
            ClientUtil.drawPlayerFace(rl, xCursor + Config.faceXOffset, y - 0.5f, alpha);
            xCursor += 11;
        }

        return fontRenderer.drawStringWithShadow(nameAndRest.toString(), xCursor, y, color);
    }

    @Redirect(
        method = "drawChat(I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I"))
    private int redirectDrawStringWithShadow(FontRenderer fontRenderer, String text, int x, int y, int color,
        @Share("chatLine") LocalRef<ChatLine> chatLineRef) {
        ChatLine currentChatLine = chatLineRef.get();

        if (Config.enableFacesInChat && currentChatLine != null) {
            List<IChatComponent> siblings = currentChatLine.func_151461_a()
                .getSiblings();
            if (siblings.size() > 2) {
                String detectedName = null;
                String commandPrefix = "/msg ";
                for (int s = 0; s < siblings.size() - 2; ++s) {
                    if (siblings.get(s)
                        .getChatStyle() != null
                        && siblings.get(s)
                            .getChatStyle()
                            .getChatClickEvent() != null
                        && siblings.get(s)
                            .getChatStyle()
                            .getChatClickEvent()
                            .getValue() != null
                        && siblings.get(s)
                            .getChatStyle()
                            .getChatClickEvent()
                            .getValue()
                            .startsWith(commandPrefix)) {
                        detectedName = siblings.get(s)
                            .getChatStyle()
                            .getChatClickEvent()
                            .getValue()
                            .substring(commandPrefix.length());
                    }
                }
                for (int s = 0; s < siblings.size() - 2; ++s) {
                    if (siblings.get(s)
                        .getUnformattedText()
                        .equals("<")) {
                        int end = -1;
                        for (int i = s + 1; i < siblings.size(); ++i) {
                            if (siblings.get(i)
                                .getUnformattedText()
                                .startsWith(">")) {
                                end = i;
                                break;
                            }
                        }

                        if (end != -1) {
                            StringBuilder prefix = new StringBuilder();
                            for (int j = 0; j < s; ++j) {
                                prefix.append(
                                    siblings.get(j)
                                        .getFormattedText());
                            }

                            StringBuilder nameAndRest = new StringBuilder();
                            for (int j = s; j < siblings.size(); ++j) {
                                nameAndRest.append(
                                    siblings.get(j)
                                        .getFormattedText());
                            }

                            int xCursor = x;
                            if (prefix.length() > 0) {
                                xCursor += fontRenderer.drawStringWithShadow(prefix.toString(), xCursor, y, color) - 1;
                            }

                            StringBuilder tokenBuilder = new StringBuilder();
                            for (int j = s + 1; j < end; ++j) {
                                tokenBuilder.append(
                                    siblings.get(j)
                                        .getUnformattedText())
                                    .append(" ");
                            }
                            String cleaned = tokenBuilder.toString()
                                .trim()
                                .replaceAll("(?i)§[0-9A-FK-OR]", "");

                            ResourceLocation rl = TabFaces.varInstanceClient.clientRegistry
                                .getTabMenuResourceLocation(detectedName == null ? cleaned : detectedName, false, -1);
                            if (rl != null) {
                                float alpha = (float) (color >> 24 & 255) / 255.0F;
                                ClientUtil.drawPlayerFace(rl, xCursor + Config.faceXOffset, y - 0.5f, alpha);
                                xCursor += 11;
                            }

                            return fontRenderer.drawStringWithShadow(nameAndRest.toString(), xCursor, y, color);
                        }
                    }

                }
            }
        }
        return fontRenderer.drawStringWithShadow(text, x, y, color);
    }
}
