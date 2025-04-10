package org.fentanylsolutions.tabfaces;

import net.minecraftforge.common.MinecraftForge;

import org.fentanylsolutions.tabfaces.command.CommandTest;
import org.fentanylsolutions.tabfaces.event.ServerEventHandler;
import org.fentanylsolutions.tabfaces.packet.PacketHandler;
import org.fentanylsolutions.tabfaces.util.Util;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry."
    public void preInit(FMLPreInitializationEvent event) {
        TabFaces.info("TabFaces version " + Tags.VERSION + " running on " + (Util.isServer() ? "Server" : "Client"));

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

    public void serverStarting(FMLServerStartingEvent event) {
        if (Util.isServer()) {
            if (TabFaces.DEBUG_MODE) {
                event.registerServerCommand(new CommandTest());
            }
        }
    }

    public void serverAboutToStart(FMLServerAboutToStartEvent event) {}

    public void serverStarted(FMLServerStartedEvent event) {}

    public void serverStopping(FMLServerStoppingEvent event) {}

    public void serverStopped(FMLServerStoppedEvent event) {}
}
