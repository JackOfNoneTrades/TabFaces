package org.fentanylsolutions.tabfaces;

import org.fentanylsolutions.tabfaces.util.Util;

import cpw.mods.fml.common.event.*;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        TabFaces.LOG
            .info("TabFaces version " + Tags.VERSION + " running on " + (Util.isServer() ? "Server" : "Client"));

        TabFaces.confFile = event.getSuggestedConfigurationFile();
        Config.synchronizeConfiguration(TabFaces.confFile);
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}

    public void serverAboutToStart(FMLServerAboutToStartEvent event) {}

    public void serverStarted(FMLServerStartedEvent event) {}

    public void serverStopping(FMLServerStoppingEvent event) {}

    public void serverStopped(FMLServerStoppedEvent event) {}
}
