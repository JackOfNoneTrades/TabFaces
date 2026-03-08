package org.fentanylsolutions.tabfaces.compat;

import cpw.mods.fml.common.Loader;

public class LoadedMods {

    public static final String SKINPORT_MODID = "skinport";
    public static final String SIMPLESKINBACKPORT_MODID = "simpleskinbackport";
    public static final String WAWELAUTH_MODID = "wawelauth";

    public static boolean skinPortLoaded = false;
    public static boolean simpleSkinBackportLoaded = false;
    public static boolean wawelAuthLoaded = false;

    public static void init() {
        skinPortLoaded = Loader.isModLoaded(SKINPORT_MODID);
        simpleSkinBackportLoaded = Loader.isModLoaded(SIMPLESKINBACKPORT_MODID);
        wawelAuthLoaded = Loader.isModLoaded(WAWELAUTH_MODID);
    }
}
