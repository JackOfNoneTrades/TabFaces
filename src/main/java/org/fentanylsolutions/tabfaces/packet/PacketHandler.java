package org.fentanylsolutions.tabfaces.packet;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.packet.packets.PacketRemoveFromCache;
import org.fentanylsolutions.tabfaces.packet.packets.UsernameUuidPairsPacket;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static SimpleNetworkWrapper net;

    public static void initPackets() {
        net = NetworkRegistry.INSTANCE.newSimpleChannel(TabFaces.MODID.toUpperCase());
        registerMessage(UsernameUuidPairsPacket.class, UsernameUuidPairsPacket.Message.class);
        registerMessage(PacketRemoveFromCache.class, PacketRemoveFromCache.Message.class);
    }

    private static int nextPacketId = 0;

    private static void registerMessage(Class packet, Class message) {
        net.registerMessage(packet, message, nextPacketId, Side.CLIENT);
        net.registerMessage(packet, message, nextPacketId, Side.SERVER);
        nextPacketId++;
    }
}
