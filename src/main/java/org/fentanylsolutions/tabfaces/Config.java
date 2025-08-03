package org.fentanylsolutions.tabfaces;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    private static Configuration config;

    public static boolean enableFacesInTabMenu;
    public static boolean showQuestionMarkIfUnknown;
    public static boolean trimTabMenu;
    public static int skinTtl;
    public static int skinTtlInterval;

    public static boolean enableFacesInServerMenu;

    public static boolean enableFacesInChat;
    public static boolean enableFacesInTabbyChat;
    public static float faceXOffset;
    public static float faceXOffsetTabbyChat;

    public static boolean debugMode;

    public static class Categories {

        public static final String general = "general";
        public static final String tabmenu = "tabmenu";
        public static final String servermenu = "servermenu";
        public static final String chat = "chat";
        public static final String debug = "debug";
    }

    public static void loadConfig(File configFile) {
        config = new Configuration(configFile);

        try {
            config.load();

            // Tab Menu
            enableFacesInTabMenu = config.getBoolean(
                "enableFacesInTabMenu",
                Categories.tabmenu,
                true,
                "Enable player faces in the server tab menu");
            showQuestionMarkIfUnknown = config.getBoolean(
                "showQuestionMarkIfUnknown",
                Categories.tabmenu,
                true,
                "Show question mark instead of Steve when a skin can't load");
            trimTabMenu = config
                .getBoolean("trimTabMenu", Categories.tabmenu, true, "Trim tab list to match number of players");
            skinTtl = config.getInt(
                "skinTtl",
                Categories.tabmenu,
                1200,
                60,
                Integer.MAX_VALUE,
                "Seconds before a skin is refreshed");
            skinTtlInterval = config.getInt(
                "skinTtlInterval",
                Categories.tabmenu,
                120,
                10,
                Integer.MAX_VALUE,
                "Interval in seconds for skin GC");

            // Server Menu
            enableFacesInServerMenu = config.getBoolean(
                "enableFacesInServerMenu",
                Categories.servermenu,
                true,
                "Enable faces in server selection menu");

            // Chat
            enableFacesInChat = config
                .getBoolean("enableFacesInChat", Categories.chat, true, "Enable player faces in chat");
            enableFacesInTabbyChat = config
                .getBoolean("enableFacesInTabbyChat", Categories.chat, true, "Enable faces in Tabby Chat");
            faceXOffset = config.getFloat("faceXOffset", Categories.chat, 1.0f, -10.0f, 10.0f, "Face X offset");
            faceXOffsetTabbyChat = config
                .getFloat("faceXOffsetTabbyChat", Categories.chat, 1.0f, -10.0f, 10.0f, "Face X offset (TabbyChat)");

            // Debug
            debugMode = config.getBoolean("debugMode", Categories.debug, false, "Enable debug logging");

        } catch (Exception e) {
            System.err.println("Error loading config: " + e.getMessage());
        } finally {
            // if (config.hasChanged()) {
            config.save();
            // }
        }
    }

    public static Configuration getRawConfig() {
        return config;
    }
}
