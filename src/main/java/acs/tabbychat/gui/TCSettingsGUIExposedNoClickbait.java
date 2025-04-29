package acs.tabbychat.gui;

import net.minecraft.client.gui.GuiButton;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.access.IMixinTCSettingSlider;
import org.fentanylsolutions.tabfaces.access.IMixinTabbyChat;
import org.fentanylsolutions.tabfaces.compat.tabbychat.TabFacesSettings;

import acs.tabbychat.core.TabbyChat;

public class TCSettingsGUIExposedNoClickbait extends TCSettingsGUI {

    public TCSettingsGUIExposedNoClickbait(TabbyChat _tc) {
        super(_tc);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button.id == TabFacesSettings.TABFACES_SETTINGS_SAVE_BUTTON_ID) {
            Config.enableFacesInTabbyChatCE
                .set(((IMixinTabbyChat) tc).getTabFacesSettings().showPlayerFaces.getTempValue());

            IMixinTCSettingSlider slider = (IMixinTCSettingSlider) ((IMixinTabbyChat) tc)
                .getTabFacesSettings().faceXOffsetSlider;
            int val = Math
                .round(slider.getSliderValue() * (slider.getMaxValue() - slider.getMinValue()) + slider.getMinValue());
            Config.faceXOffsetTabbyChatCE.set((double) val);
            Config.config.save();
        }
    }
}
