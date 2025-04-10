package org.fentanylsolutions.tabfaces.event;

import net.minecraft.server.MinecraftServer;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.util.PingUtil;
import org.fentanylsolutions.tabfaces.util.Util;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class PlayerEventHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent e) {
        /* If singleplayer, we don't do that */
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer()
            .isSinglePlayer()) {
            return;
        }

        if (Util.isServer()) {
            return;
        }

        TabFaces.varInstanceClient.clientRegistry.clear();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerLeaveFMLEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        /* If singleplayer, we don't do that */
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer()
            .isSinglePlayer()) {
            return;
        }

        if (Util.isServer()) {
            return;
        }

        TabFaces.varInstanceClient.clientRegistry.clear();
    }

    boolean wasKeyDown = false;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            return;
        }
        if (!Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && wasKeyDown) {
            TabFaces.debug("Right Key pressed.");
            PingUtil.ServerStatusCallback callback = new PingUtil.ServerStatusCallback();
            PingUtil.pingServer(callback);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            wasKeyDown = true;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            TabFaces.varInstanceClient.clientRegistry.tick();
        }
    }
}
