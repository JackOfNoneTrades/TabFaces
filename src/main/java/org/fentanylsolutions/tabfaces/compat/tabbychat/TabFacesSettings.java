package org.fentanylsolutions.tabfaces.compat.tabbychat;

import java.io.File;

import net.minecraft.client.resources.I18n;

import org.fentanylsolutions.tabfaces.Config;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.gui.PrefsButton;
import acs.tabbychat.gui.TCSettingsGUIExposedNoClickbait;
import acs.tabbychat.settings.TCSettingBool;

public class TabFacesSettings extends TCSettingsGUIExposedNoClickbait {

    private static final int SHOW_PLAYER_FACES_ID = 9201;
    public static final int TABFACES_SETTINGS_SAVE_BUTTON_ID = 9696;
    public TCSettingBool showPlayerFaces;

    public TabFacesSettings(TabbyChat tc) {
        super(tc);
        this.propertyPrefix = "settings.tabfaces";
        this.name = I18n.format(this.propertyPrefix + ".name");
        this.settingsFile = new File(tabbyChatDir, "tabfaces.cfg");
        this.bgcolor = 0x666666FF;
        this.showPlayerFaces = new TCSettingBool(
            Config.enableFacesInTabbyChat,
            "showPlayerFaces",
            this.propertyPrefix,
            SHOW_PLAYER_FACES_ID);
    }

    @Override
    public void defineDrawableSettings() {
        this.buttonList.add(this.showPlayerFaces);
    }

    @Override
    public void initDrawableSettings() {
        int col1x = ((this.width - 300) / 2) + 55;
        int buttonColor = (this.bgcolor & 0xFFFFFF) - 0x1000000;

        this.showPlayerFaces.setButtonLoc(col1x, rowY(1));
        this.showPlayerFaces.setLabelLoc(col1x + 19);
        this.showPlayerFaces.buttonColor = buttonColor;
    }

    @Override
    public void initGui() {
        super.initGui();
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
