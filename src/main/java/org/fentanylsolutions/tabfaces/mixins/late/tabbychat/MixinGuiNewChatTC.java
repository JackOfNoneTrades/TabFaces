package org.fentanylsolutions.tabfaces.mixins.late.tabbychat;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.util.ClientUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;

import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TCChatLine;

@SuppressWarnings("unused")
@Mixin(GuiNewChatTC.class)
public abstract class MixinGuiNewChatTC {

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
        method = "drawChat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I"))
    private int injectedDrawStringWithFace(FontRenderer fontRenderer, String text, int x, int y, int color,
        @Local List<TCChatLine> msgList, @Local(ordinal = 7) int i, @Local(ordinal = 11) int textOpacity,
        @Local(ordinal = 8) int yOrigin) {

        TCChatLine currentChatLine = msgList.get(i);

        if (Config.enableFacesInTabbyChat && currentChatLine != null) {
            List<IChatComponent> siblings = currentChatLine.getChatComponent()
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
                        if (siblings.get(s)
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
                }
                for (int s = 0; s < siblings.size() - 2; ++s) {
                    if (siblings.get(s)
                        .getUnformattedText()
                        .equals("<")) {
                        int end = -1;
                        for (int i_ = s + 1; i_ < siblings.size(); ++i_) {
                            if (siblings.get(i_)
                                .getUnformattedText()
                                .startsWith(">")) {
                                end = i_;
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
