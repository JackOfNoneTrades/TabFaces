package org.fentanylsolutions.tabfaces.event;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.util.Util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class PlayerEventHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onOtherPlayerJoin(EntityJoinWorldEvent e) {
        /* If singleplayer, we don't do that */
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer()
            .isSinglePlayer()) {
            return;
        }

        if (!(e.entity instanceof EntityOtherPlayerMP)) {
            return;
        }

        if (Util.isServer()) {
            return;
        }

        String displayName = ((AbstractClientPlayer) e.entity).getDisplayName();
        ResourceLocation rl = ((AbstractClientPlayer) e.entity).getLocationSkin();

        TabFaces.debug("Detected player join event: " + displayName);
        TabFaces.varInstanceClient.clientRegistry.insert(displayName, rl);
        TabFaces.debug("Player " + displayName + "joined");
    }

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

        // System.out.println("We left a world");
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

        // ClientSkinUtil.clearSkinCache();
        TabFaces.varInstanceClient.clientRegistry.clear();
        // System.out.println("Exited MP world");
        // OfflineAuth.varInstanceClient.onDedicatedServer = false;
    }

    /*
     * @SuppressWarnings("unused")
     * @SubscribeEvent
     * public void onTick(TickEvent.RenderTickEvent event)
     * {
     * int sped = 1;
     * if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
     * TabFaces.varInstanceClient.u -= sped;
     * System.out.println("u: " + TabFaces.varInstanceClient.u);
     * } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
     * TabFaces.varInstanceClient.u += sped;
     * System.out.println("u: " + TabFaces.varInstanceClient.u);
     * }
     * if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
     * TabFaces.varInstanceClient.v -= sped;
     * System.out.println("v: " + TabFaces.varInstanceClient.v);
     * } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
     * TabFaces.varInstanceClient.v += sped;
     * System.out.println("v: " + TabFaces.varInstanceClient.v);
     * }
     * if (Keyboard.isKeyDown(Keyboard.KEY_UP) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
     * TabFaces.varInstanceClient.uWidth -= sped;
     * System.out.println("uwidth: " + TabFaces.varInstanceClient.uWidth);
     * } else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
     * TabFaces.varInstanceClient.uWidth += sped;
     * System.out.println("uwidth: " + TabFaces.varInstanceClient.uWidth);
     * }
     * if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
     * TabFaces.varInstanceClient.vHeight -= sped;
     * System.out.println("vheight: " + TabFaces.varInstanceClient.vHeight);
     * } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
     * TabFaces.varInstanceClient.vHeight += sped;
     * System.out.println("vheight: " + TabFaces.varInstanceClient.vHeight);
     * }
     * }
     */
}
