package org.fentanylsolutions.tabfaces.util;

import java.net.InetAddress;

import net.minecraft.client.Minecraft;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.registries.ClientRegistry;

import com.mojang.authlib.GameProfile;

public class PingUtil {

    public static final int DEFAULT_PORT = 25565;

    public static String[] parseAddress(String input) {
        String address;
        int port = DEFAULT_PORT;

        if (input.contains(":")) {
            String[] parts = input.split(":", 2);
            address = parts[0];
            try {
                port = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default: " + DEFAULT_PORT);
            }
        } else {
            address = input;
        }

        return new String[] { address, String.valueOf(port) };
    }

    public static class ServerStatusCallback {

        public void onResponse(ServerStatusResponse response) {
            TabFaces.info(response.toString());
            ServerStatusResponse.PlayerCountData playerData = response.func_151318_b();

            if (playerData != null) {
                int online = playerData.func_151333_b();
                int max = playerData.func_151332_a();

                TabFaces.info("Players: " + online + " / " + max);

                GameProfile[] sample = playerData.func_151331_c();
                if (sample != null && sample.length > 0) {
                    TabFaces.info("Sampled Players:");
                    for (GameProfile profile : sample) {
                        TabFaces.info("- " + profile.getName() + " (" + profile.getId() + ")");
                    }
                } else {
                    TabFaces.info("No player sample provided by server.");
                }
            } else {
                TabFaces.warn("No player data in server response.");
            }
        }

        public void onFailure(Throwable t) {
            TabFaces.error("Failed to ping.");
            t.printStackTrace();
        }
    }

    /* The real deal */
    public static class ServerStatusCallbackClientRegistry extends ServerStatusCallback {

        @Override
        public void onResponse(ServerStatusResponse response) {
            TabFaces.info(response.toString());
            ServerStatusResponse.PlayerCountData playerData = response.func_151318_b();

            if (playerData != null) {
                GameProfile[] profiles = playerData.func_151331_c();
                if (profiles != null && profiles.length > 0) {
                    for (GameProfile profile : profiles) {
                        ClientRegistry.Data res = TabFaces.varInstanceClient.clientRegistry
                            .getByDisplayName(profile.getName());
                        if (res == null || res.id == null) {
                            TabFaces.varInstanceClient.clientRegistry
                                .insert(profile.getName(), profile.getId(), null, false, -1);
                        }
                    }
                } else {
                    TabFaces.debug("No players provided by server.");
                }
            } else {
                TabFaces.debug("No player data in server response.");
            }
        }
    }

    public static void pingServer(ServerStatusCallback callback) {
        String[] addressPair = parseAddress(ClientUtil.minecraftInstance.func_147104_D().serverIP);

        try {
            NetworkManager networkManager = NetworkManager
                .provideLanClient(InetAddress.getByName(addressPair[0]), Integer.parseInt(addressPair[1]));

            networkManager.setNetHandler(new INetHandlerStatusClient() {

                private boolean receivedInfo = false;

                @Override
                public void handleServerInfo(S00PacketServerInfo packetIn) {
                    receivedInfo = true;
                    ServerStatusResponse response = packetIn.func_149294_c();
                    if (callback != null) {
                        callback.onResponse(response);
                    }
                    networkManager.scheduleOutboundPacket(new C01PacketPing(Minecraft.getSystemTime()));
                }

                @Override
                public void handlePong(S01PacketPong packetIn) {
                    networkManager.closeChannel(new ChatComponentText("Ping complete"));
                }

                @Override
                public void onDisconnect(IChatComponent reason) {
                    if (!receivedInfo) {
                        if (callback != null) {
                            callback.onFailure(
                                new RuntimeException("Failed to ping server: " + reason.getUnformattedText()));
                        }
                    }
                }

                @Override
                public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState) {}

                @Override
                public void onNetworkTick() {}
            });

            networkManager.scheduleOutboundPacket(
                new C00Handshake(5, addressPair[0], Integer.parseInt(addressPair[1]), EnumConnectionState.STATUS));
            networkManager.scheduleOutboundPacket(new C00PacketServerQuery());

        } catch (Throwable t) {
            if (callback != null) {
                callback.onFailure(t);
            }
            TabFaces.error(t.getMessage());
        }
    }
}
