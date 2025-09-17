package org.fentanylsolutions.tabfaces.compat;

import cpw.mods.fml.common.Loader;

public class LoadedMods {

    public static final String SKINPORT_MODID = "skinport";
    public static final String SIMPLESKINBACKPORT_MODID = "simpleskinbackport";

    public static boolean skinPortLoaded = false;
    public static boolean simpleSkinBackportLoaded = false;

    public static void init() {
        skinPortLoaded = Loader.isModLoaded(SKINPORT_MODID);
        simpleSkinBackportLoaded = Loader.isModLoaded(SIMPLESKINBACKPORT_MODID);
    }
}
