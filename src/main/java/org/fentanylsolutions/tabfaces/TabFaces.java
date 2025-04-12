package org.fentanylsolutions.tabfaces;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = TabFaces.MODID,
    version = Tags.VERSION,
    name = "TabFaces",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:unimixins;required-after:carbonconfig",
    acceptableRemoteVersions = "*")
public class TabFaces {

    public static final String MODID = "tabfaces";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static VarInstanceClient varInstanceClient;
    public static boolean DEBUG_MODE;

    @SidedProxy(
        clientSide = "org.fentanylsolutions.tabfaces.ClientProxy",
        serverSide = "org.fentanylsolutions.tabfaces.CommonProxy")
    public static CommonProxy proxy;

    @SuppressWarnings("unused")
    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry."
    public void preInit(FMLPreInitializationEvent event) {
        String debugVar = System.getenv("MCMODDING_DEBUG_MODE");
        DEBUG_MODE = debugVar != null;
        TabFaces.info("Debugmode: " + DEBUG_MODE);
        proxy.preInit(event);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    public static void debug(String message) {
        if (DEBUG_MODE || Config.debugMode) {
            LOG.info("DEBUG: " + message);
        }
    }

    public static void info(String message) {
        LOG.info(message);
    }

    public static void warn(String message) {
        LOG.warn(message);
    }

    public static void error(String message) {
        LOG.error(message);
    }
}
