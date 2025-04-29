package org.fentanylsolutions.tabfaces.mixins.late.tabbychat;

import org.fentanylsolutions.tabfaces.access.IMixinTCSettingSlider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import acs.tabbychat.settings.TCSettingSlider;

@Mixin(TCSettingSlider.class)
public class MixinTCSettingSlider implements IMixinTCSettingSlider {

    @Shadow(remap = false)
    float minValue;

    @Shadow(remap = false)
    float maxValue;

    @Shadow(remap = false)
    float sliderValue;

    @Override
    public float getMinValue() {
        return minValue;
    }

    @Override
    public float getMaxValue() {
        return maxValue;
    }

    @Override
    public float getSliderValue() {
        return sliderValue;
    }
}
