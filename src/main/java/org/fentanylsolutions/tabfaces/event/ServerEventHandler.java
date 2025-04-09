package org.fentanylsolutions.tabfaces.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.packet.PacketHandler;
import org.fentanylsolutions.tabfaces.packet.packets.PacketRemoveFromCache;
import org.fentanylsolutions.tabfaces.packet.packets.UsernameUuidPairsPacket;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class ServerEventHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        TabFaces.debug("Player joined server: " + e.player.getDisplayName());
        GameProfile gameProfile = e.player.getGameProfile();

        // This one gets sent to all users
        List<UsernameUuidPairsPacket.UsernameUuidPair> singlePair = new ArrayList<>();
        singlePair.add(
            new UsernameUuidPairsPacket.UsernameUuidPair(
                gameProfile.getName(),
                gameProfile.getId()
                    .toString()));
        UsernameUuidPairsPacket.Message msg = new UsernameUuidPairsPacket.Message(singlePair);
        PacketHandler.net.sendToAll(msg);

        // This gets sent to the joining user only
        List<UsernameUuidPairsPacket.UsernameUuidPair> pairs = new ArrayList<>();
        for (EntityPlayerMP player : MinecraftServer.getServer()
            .getConfigurationManager().playerEntityList) {
            pairs.add(
                new UsernameUuidPairsPacket.UsernameUuidPair(
                    player.getDisplayName(),
                    player.getUniqueID()
                        .toString()));
        }

        msg = new UsernameUuidPairsPacket.Message(pairs);
        PacketHandler.net.sendTo(msg, (EntityPlayerMP) e.player);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerLEave(PlayerEvent.PlayerLoggedOutEvent e) {
        PacketRemoveFromCache.Message msg = new PacketRemoveFromCache.Message(e.player.getDisplayName());
        PacketHandler.net.sendToAll(msg);
    }
}
