package org.fentanylsolutions.tabfaces;

import net.minecraftforge.common.MinecraftForge;

import org.fentanylsolutions.tabfaces.event.ServerEventHandler;
import org.fentanylsolutions.tabfaces.packet.PacketHandler;
import org.fentanylsolutions.tabfaces.util.Util;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceServer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        TabFaces.LOG
            .info("TabFaces version " + Tags.VERSION + " running on " + (Util.isServer() ? "Server" : "Client"));

        if (Util.isServer()) {
            TabFaces.varInstanceServer = new VarInstanceServer();
        }
        TabFaces.confFile = event.getSuggestedConfigurationFile();
        Config.synchronizeConfiguration(TabFaces.confFile);

        PacketHandler.initPackets();

        if (Util.isServer()) {
            ServerEventHandler serverEventHandler = new ServerEventHandler();
            MinecraftForge.EVENT_BUS.register(serverEventHandler);
            FMLCommonHandler.instance()
                .bus()
                .register(serverEventHandler);
        }
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}

    public void serverAboutToStart(FMLServerAboutToStartEvent event) {}

    public void serverStarted(FMLServerStartedEvent event) {}

    public void serverStopping(FMLServerStoppingEvent event) {}

    public void serverStopped(FMLServerStoppedEvent event) {}
}
