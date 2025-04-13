package org.fentanylsolutions.tabfaces;

import carbonconfiglib.CarbonConfig;
import carbonconfiglib.config.ConfigEntry;
import carbonconfiglib.config.ConfigHandler;
import carbonconfiglib.config.ConfigSection;

public class Config {

    public static ConfigHandler config;

    private static class Defaults {

        /* client */
        public static final boolean showQuestionMarkIfUnknown = true;
        public static final int skinTtl = 1200;
        public static final int skinTtlInterval = 120;
        public static final boolean enableFacesInTabMenu = true;
        public static final boolean enableFacesInServerMenu = true;
        public static final boolean enableFacesInChat = true;
        public static final boolean trimTabMenu = true;

        /* common */
        public static final boolean debugMode = false;
    }

    public static class Categories {

        public static final String general = "general";
        public static final String tabmenu = "Tab Menu";
        public static final String servermenu = "Server Selection Menu";
        public static final String chat = "Chat";
        public static final String debug = "Debug";
    }

    /* Tab */
    public static boolean enableFacesInTabMenu = Defaults.enableFacesInTabMenu;
    public static ConfigEntry.BoolValue enableFacesInTabMenuCE;
    public static boolean showQuestionMarkIfUnknown = Defaults.showQuestionMarkIfUnknown;
    public static ConfigEntry.BoolValue showQuestionMarkIfUnknownCE;
    public static boolean trimTabMenu = Defaults.trimTabMenu;
    public static ConfigEntry.BoolValue trimTabMenuCE;
    public static int skinTtl = Defaults.skinTtl;
    public static ConfigEntry.IntValue skinTtlCE;
    public static int skinTtlInterval = Defaults.skinTtlInterval;
    public static ConfigEntry.IntValue skinTtlIntervalCE;

    /* Server List */
    public static boolean enableFacesInServerMenu = Defaults.enableFacesInServerMenu;
    public static ConfigEntry.BoolValue enableFacesInServerMenuCE;

    /* Chat */
    public static boolean enableFacesInChat = Defaults.enableFacesInChat;
    public static ConfigEntry.BoolValue enableFacesInChatCE;

    /* Debug */
    public static boolean debugMode = Defaults.debugMode;
    public static ConfigEntry.BoolValue debugModeCE;

    public static void registerConfig() {
        carbonconfiglib.config.Config conf = new carbonconfiglib.config.Config(TabFaces.MODID);

        /* Tab */
        ConfigSection tabmenuSection = conf.add(Categories.tabmenu);
        enableFacesInTabMenuCE = tabmenuSection.addBool(
            "enableFacesInTabMenu",
            Defaults.enableFacesInTabMenu,
            "Enable player faces in the server tab menu");
        showQuestionMarkIfUnknownCE = tabmenuSection.addBool(
            "showQuestionMarkIfUnknown",
            Defaults.showQuestionMarkIfUnknown,
            "Show a question mark texture instead of Steve when a skin cannot be loaded");
        trimTabMenuCE = tabmenuSection
            .addBool("trimTabMenu", Defaults.trimTabMenu, "Display only as much rows as there are connected players");
        skinTtlCE = tabmenuSection
            .addInt("skinTtl", Defaults.skinTtl, "How many seconds elapse before a skin is refreshed");
        skinTtlIntervalCE = tabmenuSection.addInt(
            "skinTtlInterval",
            Defaults.skinTtlInterval,
            "Interval in seconds at which the skin garbage collection runs");

        /* Server List */
        ConfigSection servermenuSection = conf.add(Categories.servermenu);
        enableFacesInServerMenuCE = servermenuSection.addBool(
            "enableFacesInServerMenu",
            Defaults.enableFacesInServerMenu,
            "Enable player faces in the server selection menu");

        /* Chat */
        ConfigSection chatSection = conf.add(Categories.chat);
        enableFacesInChatCE = chatSection
            .addBool("enableFacesInChat", Defaults.enableFacesInChat, "Enable player faces in the chat");

        /* Debug */
        ConfigSection debugSection = conf.add(Categories.debug);
        debugModeCE = debugSection.addBool("debugMode", Defaults.debugMode);

        config = CarbonConfig.CONFIGS.createConfig(conf);
        config.addLoadedListener(() -> {
            TabFaces.debug("Carbon config callback, dumping vars.");
            dumpConf();
        });
        config.register();
    }

    private static void dumpConf() {
        /* Tab */
        enableFacesInTabMenu = enableFacesInTabMenuCE.get();
        showQuestionMarkIfUnknown = showQuestionMarkIfUnknownCE.get();
        trimTabMenu = trimTabMenuCE.get();
        skinTtl = skinTtlCE.get();
        skinTtlInterval = skinTtlIntervalCE.get();

        /* Server List */
        enableFacesInServerMenu = enableFacesInServerMenuCE.get();

        /* Chat */
        enableFacesInChat = enableFacesInChatCE.get();

        /* Debug */
        debugMode = debugModeCE.get();
    }
}
