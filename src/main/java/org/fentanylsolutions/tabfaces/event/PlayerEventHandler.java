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
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            // If the Right arrow key is not pressed and we previously detected it being pressed
            if (!Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && wasKeyDown) {
                TabFaces.debug("Right Key pressed.");
                PingUtil.ServerStatusCallbackClientRegistry callback = new PingUtil.ServerStatusCallbackClientRegistry();
                PingUtil.pingServer(callback);

                // Reset the wasKeyDown state to prevent continuous triggering
                wasKeyDown = false;
            }
            // If the Right arrow key is pressed, set the wasKeyDown flag
            else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && !wasKeyDown) {
                wasKeyDown = true;
            }
        }
        // Reset the wasKeyDown state if LShift is not pressed anymore
        else {
            wasKeyDown = false;
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
