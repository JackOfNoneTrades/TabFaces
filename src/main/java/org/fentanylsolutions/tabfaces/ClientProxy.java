package org.fentanylsolutions.tabfaces;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import org.fentanylsolutions.tabfaces.event.PlayerEventHandler;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        TabFaces.varInstanceClient = new VarInstanceClient();
        MinecraftForge.EVENT_BUS.register(new GameOverlayGuiHandler(TabFaces.minecraftRef));

        PlayerEventHandler playerEventHandler = new PlayerEventHandler();
        MinecraftForge.EVENT_BUS.register(playerEventHandler);
        FMLCommonHandler.instance()
            .bus()
            .register(playerEventHandler);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        super.serverAboutToStart(event);
    }

    // register server commands in this event handler
    public void serverStarting(FMLServerStartingEvent event) {
        super.serverStarting(event);
    }

    public void serverStarted(FMLServerStartedEvent event) {
        super.serverStarted(event);
    }

    public void serverStopping(FMLServerStoppingEvent event) {
        super.serverStopping(event);
    }

    public void serverStopped(FMLServerStoppedEvent event) {
        super.serverStopped(event);
    }
}
