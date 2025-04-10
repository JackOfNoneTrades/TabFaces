package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.gui.Gui;
import org.fentanylsolutions.tabfaces.access.IMixinGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Gui.class)
public class MixinGui implements IMixinGui {
    @Shadow
    float zLevel;

    @Shadow
    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {}

    @Override
    public void setZLevel(float level) {
        zLevel = level;
    }

    @Override
    public void drawGradientRectPub(int left, int top, int right, int bottom, int startColor, int endColor) {
        drawGradientRect(left, top, right, bottom, startColor, endColor);
    }
}
