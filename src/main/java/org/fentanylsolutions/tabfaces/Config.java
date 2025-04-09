package org.fentanylsolutions.tabfaces;

import java.io.File;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

    private static class Defaults {

        /* client */
        public static final boolean showQuestionMarkIfUnknown = true;
        public static final int skinTtl = 120;
        public static final int skinTtlInterval = 120;

        /* common */
        public static final boolean debugMode = false;
    }

    public static class Categories {

        public static final String client = "client";
        public static final String common = "common";
    }

    public static boolean debugMode = Defaults.debugMode;
    public static boolean showQuestionMarkIfUnknown = Defaults.showQuestionMarkIfUnknown;
    public static int skinTtl = Defaults.skinTtl;
    public static int skinTtlInterval = Defaults.skinTtlInterval;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        Property showQuestionMarkIfUnknownProperty = configuration.get(
            Categories.client,
            "showQuestionMarkIfUnknown",
            Defaults.showQuestionMarkIfUnknown,
            "Should show question mark if player skin unknown? Otherwise shows steve's face.");
        showQuestionMarkIfUnknown = showQuestionMarkIfUnknownProperty.getBoolean();

        skinTtl = configuration.getInt(
            "skinTtl",
            Categories.client,
            Defaults.skinTtl,
            15,
            Integer.MAX_VALUE,
            "Skin resource refresh time in seconds.");
        skinTtlInterval = configuration.getInt(
            "skinTtlInterval",
            Categories.client,
            Defaults.skinTtlInterval,
            15,
            Integer.MAX_VALUE,
            "How often should the skin cache invalidator run in seconds.");

        Property debugModeProperty = configuration
            .get(Categories.common, "debugMode", Defaults.debugMode, "Enable/disable debug logs");
        debugMode = debugModeProperty.getBoolean();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static ConfigCategory getConfigCategoryByString(String category) {
        return (new Configuration(TabFaces.confFile).getCategory(category));
    }
}
