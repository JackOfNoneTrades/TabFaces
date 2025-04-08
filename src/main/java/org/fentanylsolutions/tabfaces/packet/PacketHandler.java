package org.fentanylsolutions.tabfaces.packet;

import org.fentanylsolutions.tabfaces.packet.packets.PushSkinbytesToClientPacket;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static SimpleNetworkWrapper net;

    public static void initPackets() {
        net = NetworkRegistry.INSTANCE.newSimpleChannel("tabfaces".toUpperCase());
        registerMessage(PushSkinbytesToClientPacket.class, PushSkinbytesToClientPacket.SimpleMessage.class);
        // registerMessage(PushAllSkinDataToClient.class, PushAllSkinDataToClient.SimpleMessage.class);
    }

    private static int nextPacketId = 0;

    private static void registerMessage(Class packet, Class message) {
        net.registerMessage(packet, message, nextPacketId, Side.CLIENT);
        net.registerMessage(packet, message, nextPacketId, Side.SERVER);
        nextPacketId++;
    }
}
