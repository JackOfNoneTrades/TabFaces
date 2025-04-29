package org.fentanylsolutions.tabfaces.compat.tabbychat;

import net.minecraft.client.resources.I18n;

import org.fentanylsolutions.tabfaces.Config;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.gui.PrefsButton;
import acs.tabbychat.gui.TCSettingsGUIExposedNoClickbait;
import acs.tabbychat.settings.TCSettingBool;
import acs.tabbychat.settings.TCSettingSlider;

public class TabFacesSettings extends TCSettingsGUIExposedNoClickbait {

    private static final int SHOW_PLAYER_FACES_ID = 11000;
    private static final int FACE_X_OFFSET_ID = 11001;
    public static final int TABFACES_SETTINGS_SAVE_BUTTON_ID = 9696;
    public TCSettingBool showPlayerFaces;
    public TCSettingSlider faceXOffsetSlider;

    public TabFacesSettings(TabbyChat tc) {
        super(tc);
        this.propertyPrefix = "settings.tabfaces";
        this.name = I18n.format(this.propertyPrefix + ".name");
        this.settingsFile = null;
        this.bgcolor = 0x666666FF;
        this.showPlayerFaces = new TCSettingBool(
            Config.enableFacesInTabbyChat,
            "showPlayerFaces",
            this.propertyPrefix,
            SHOW_PLAYER_FACES_ID);
        System.out.println("SNEED");
        System.out.println(Config.faceXOffset);
        this.faceXOffsetSlider = new TCSettingSlider(
            Config.faceXOffsetTabbyChat,
            "faceXOffset",
            this.propertyPrefix,
            FACE_X_OFFSET_ID,
            -10f,
            10f);
    }

    @Override
    public void defineDrawableSettings() {
        this.buttonList.add(this.showPlayerFaces);
        this.buttonList.add(this.faceXOffsetSlider);
    }

    @Override
    public void initDrawableSettings() {
        int col1x = ((this.width - 300) / 2) + 55;
        int buttonColor = (this.bgcolor & 0xFFFFFF) - 0x1000000;

        this.showPlayerFaces.setButtonLoc(col1x, rowY(1));
        this.showPlayerFaces.setLabelLoc(col1x + 19);
        this.showPlayerFaces.buttonColor = buttonColor;

        this.faceXOffsetSlider.setLabelLoc(col1x);
        this.faceXOffsetSlider.setButtonLoc(
            col1x + 5 + this.mc.fontRenderer.getStringWidth(this.faceXOffsetSlider.description),
            this.rowY(2));
        this.faceXOffsetSlider.buttonColor = buttonColor;
        this.faceXOffsetSlider.units = "";
        this.faceXOffsetSlider.setValue(Config.faceXOffsetTabbyChat);
    }

    @Override
    public void initGui() {
        super.initGui();
        // Remove original save button
        this.buttonList.removeIf(button -> button.id == 8901);
        int effRight = (this.width + 300) / 2;
        int bW = 40;
        int bH = 14;
        PrefsButton savePrefs = new PrefsButton(
            TABFACES_SETTINGS_SAVE_BUTTON_ID,
            effRight + 10,
            (this.height + 180) / 2 - bH,
            bW,
            bH,
            I18n.format("settings.save", new Object[0]));
        this.buttonList.add(savePrefs);
    }
}
