package org.fentanylsolutions.tabfaces.compat;

import cpw.mods.fml.common.Loader;

public class LoadedMods {

    public static boolean skinPortLoaded = false;

    public static void init() {
        skinPortLoaded = Loader.isModLoaded("skinport");
    }
}
