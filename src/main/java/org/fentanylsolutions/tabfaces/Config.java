package org.fentanylsolutions.tabfaces;

import java.io.File;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

    private static class Defaults {

        /* client */
        public static final int maxAcceptedSkinBytes = 500000;
        public static final boolean showQuestionMarkIfUnknown = true;
        public static final boolean alignToLeftIfNoQuestion = false;

        /* common */
        public static final boolean debugMode = false;
    }

    public static class Categories {

        public static final String client = "client";

    }

    public static boolean debugMode = Defaults.debugMode;
    public static boolean showQuestionMarkIfUnknown = Defaults.showQuestionMarkIfUnknown;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        Property showQuestionMarkIfUnknownProperty = configuration.get(
            Categories.client,
            "showQuestionMarkIfUnknown",
            Defaults.showQuestionMarkIfUnknown,
            "Should show question mark if player skin unknown? Otherwise shows steve's face.");
        showQuestionMarkIfUnknown = showQuestionMarkIfUnknownProperty.getBoolean();

        Property debugModeProperty = configuration
            .get(Categories.client, "debugMode", Defaults.debugMode, "Enable/disable debug logs");
        debugMode = debugModeProperty.getBoolean();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static ConfigCategory getConfigCategoryByString(String category) {
        return (new Configuration(TabFaces.confFile).getCategory(category));
    }
}
