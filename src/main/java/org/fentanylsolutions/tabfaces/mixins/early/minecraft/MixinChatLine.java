package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.IChatComponent;

import org.fentanylsolutions.tabfaces.access.IMixinChatLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("unused")
@Mixin(ChatLine.class)
public class MixinChatLine implements IMixinChatLine {

    @Shadow
    IChatComponent lineString;

    @Override
    public IChatComponent getLineString() {
        return lineString;
    }
}
