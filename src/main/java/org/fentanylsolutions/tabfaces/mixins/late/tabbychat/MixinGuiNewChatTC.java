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

import com.llamalad7.mixinextras.sugar.Local;

import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TCChatLine;
import org.spongepowered.asm.mixin.injection.Redirect;


@SuppressWarnings("unused")
@Mixin(GuiNewChatTC.class)
public abstract class MixinGuiNewChatTC {

    @Redirect(
        method = "drawChat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I"
        )
    )
    private int injectedDrawStringWithFace(FontRenderer fontRenderer, String text, int x, int y, int color,
                                           @Local List<TCChatLine> msgList,
                                           @Local(ordinal = 7) int i,
                                           @Local(ordinal = 11) int textOpacity,
                                           @Local(ordinal = 8) int yOrigin) {

        List<IChatComponent> siblings = msgList.get(i)
            .getChatComponent()
            .getSiblings();

        if (!Config.enableFacesInTabbyChat || siblings.size() <= 2) {
            return fontRenderer.drawStringWithShadow(text, x, y, color);
        }

        for (int s = 0; s < siblings.size() - 2; ++s) {
            if (siblings.get(s).getUnformattedText().equals("<")
                && siblings.get(s + 2).getUnformattedText().equals("> ")) {

                String prefix = "";
                for (int j = 0; j < s; ++j) {
                    prefix += siblings.get(j).getFormattedText();
                }

                String nameAndRest = "";
                for (int j = s; j < siblings.size(); ++j) {
                    nameAndRest += siblings.get(j).getFormattedText();
                }

                int xCursor = x;
                if (!prefix.isEmpty()) {
                    xCursor += fontRenderer.drawStringWithShadow(prefix, xCursor, y, color) - 1;
                }

                ResourceLocation rl = TabFaces.varInstanceClient.clientRegistry.getTabMenuResourceLocation(
                    siblings.get(s + 1).getUnformattedText(),
                    false,
                    -1
                );

                if (rl != null) {
                    float alpha = (float) textOpacity / 255.0F;
                    ClientUtil.drawPlayerFace(rl, xCursor, yOrigin + 0.5f, alpha);
                    xCursor += 11;
                }

                return fontRenderer.drawStringWithShadow(nameAndRest, xCursor, y, color);
            }
        }

        return fontRenderer.drawStringWithShadow(text, x, yOrigin + 1, color);
    }
}
