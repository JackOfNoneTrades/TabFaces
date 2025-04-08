package org.fentanylsolutions.tabfaces.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class ConfigGui extends GuiConfig {

    private static IConfigElement ce = new ConfigElement(Config.getConfigCategoryByString(Config.Categories.client));

    public ConfigGui(GuiScreen parent) {
        // this.parentScreen = parent;
        super(
            parent,
            ImmutableList.of(ce),
            "tabfaces",
            "tabfaces",
            false,
            false,
            I18n.format("tabfaces.configgui.title"),
            TabFaces.confFile.getAbsolutePath());
        TabFaces.debug("Instantiating config gui");
    }

    @Override
    public void initGui() {
        // You can add buttons and initialize fields here
        super.initGui();
        TabFaces.debug("Initializing config gui");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // You can do things like create animations, draw additional elements, etc. here
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        TabFaces.debug("Config button id " + b.id + " pressed");
        super.actionPerformed(b);
        /* "Done" button */
        if (b.id == 2000) {
            /* Syncing config */
            Config.synchronizeConfiguration(TabFaces.confFile);
        }
    }

}
