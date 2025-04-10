package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;

import org.fentanylsolutions.tabfaces.access.IMixinGuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiScreen.class)
public class MixinGuiScreen implements IMixinGuiScreen {

    @Shadow
    static RenderItem itemRender;

    @Override
    public RenderItem getItemRender() {
        return itemRender;
    }
}
