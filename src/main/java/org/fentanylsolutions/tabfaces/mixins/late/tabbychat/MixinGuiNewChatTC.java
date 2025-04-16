package org.fentanylsolutions.tabfaces.mixins.late.tabbychat;

import java.util.List;

import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.util.ClientUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.sugar.Local;

import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TCChatLine;

@SuppressWarnings("unused")
@Mixin(GuiNewChatTC.class)
public abstract class MixinGuiNewChatTC {

    @ModifyArg(
        remap = false,
        method = "drawChat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I"),
        index = 1)
    private int injectedXOrigin(int xOrigin, @Local List<TCChatLine> msgList, @Local(ordinal = 7) int i,
        @Local(ordinal = 11) int textOpacity, @Local(ordinal = 8) int yOrigin) {

        List<IChatComponent> siblings = msgList.get(i)
            .getChatComponent()
            .getSiblings();
        if (Config.enableFacesInChat) {
            if (siblings.size() > 2) {
                if (siblings.get(0)
                    .getUnformattedText()
                    .equals("<")
                    && siblings.get(2)
                        .getUnformattedText()
                        .equals("> ")) {
                    ResourceLocation rl = TabFaces.varInstanceClient.clientRegistry.getTabMenuResourceLocation(
                        siblings.get(1)
                            .getUnformattedText(),
                        false,
                        -1);
                    if (rl != null) {
                        float alpha = ((float) (textOpacity)) / 255.0F;
                        ClientUtil.drawPlayerFace(rl, xOrigin + 1, yOrigin + 0.5f, alpha);
                        return xOrigin + 11;
                    }
                }
            }
        }
        return xOrigin;
    }
}
