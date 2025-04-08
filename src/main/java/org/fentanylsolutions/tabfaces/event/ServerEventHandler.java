package org.fentanylsolutions.tabfaces.event;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayerMP;

import org.apache.commons.io.IOUtils;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.packet.PacketHandler;
import org.fentanylsolutions.tabfaces.packet.packets.PushSkinbytesToClientPacket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ServerEventHandler {

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        TabFaces.debug("Player joined server: " + e.player.getDisplayName());
        GameProfile gameProfile = e.player.getGameProfile();
        if (gameProfile == null || gameProfile.getProperties()
            .get("textures") == null) {
            TabFaces.error(
                "Could not get game profile of player " + e.player.getDisplayName()
                    + ", the server should be running in online mode");
            return;
        }
        /* The collection should only have one element */
        Collection<Property> propCollection = gameProfile.getProperties()
            .get("textures");
        if (!propCollection.iterator()
            .hasNext()) {
            TabFaces.error(
                "Could not get game profile of player " + e.player.getDisplayName()
                    + ", the server should be running in online mode");
            return;
        }
        Property prop = propCollection.iterator()
            .next();
        /* this gets us something like this: */
        /*
         * {"timestamp":xxx,"profileId":"xxx","profileName":"xxx","textures":{"SKIN":{"url":
         * "http://textures.minecraft.net/texture/xxx"}}}
         */
        String textureJsonB64 = prop.getValue();
        byte[] textureDecodedBytes = Base64.getDecoder()
            .decode(textureJsonB64);

        String textureJson = new String(textureDecodedBytes);
        Gson gson = new Gson();

        // JsonValue jsonValue = Json.parse(textureJson);
        // JsonObject jsonObject = jsonValue.asObject();
        // String skinUrl = jsonObject.get("textures").asObject().get("SKIN").asObject().get("url").asString();

        JsonObject jsonObject = gson.fromJson(textureJson, JsonObject.class);
        JsonObject texturesObject = jsonObject.getAsJsonObject("textures");
        if (texturesObject == null || !texturesObject.has("SKIN")) {
            TabFaces.error("No 'SKIN' key found in textures JSON.");
            return;
        }
        JsonObject skinObject = texturesObject.getAsJsonObject("SKIN");
        String skinUrl = skinObject.get("url")
            .getAsString();

        // String skinUrl = "https://i.imgur.com/OQnpEfv.png";
        // skinUrl = "https://i.imgur.com/HTyhAK2.png";
        TabFaces.debug("skinUrl: " + skinUrl);
        if (skinUrl == null) {
            TabFaces.error("skinUrl is null");
            return;
        }

        class DownloadThread extends Thread {

            public void start(String skinUrl, String displayName) {
                BufferedInputStream in;
                try {
                    in = new BufferedInputStream(new URL(skinUrl).openStream());
                } catch (IOException ex) {
                    TabFaces.error("Failed to download skin");
                    ex.printStackTrace();
                    return;
                }
                byte[] skinBytes;
                try {
                    skinBytes = IOUtils.toByteArray(in);
                } catch (IOException ex) {
                    TabFaces.error("Failed convert skin input stream to byte array");
                    ex.printStackTrace();
                    return;
                }
                if (skinBytes != null && skinBytes.length > 0) {
                    TabFaces.varInstanceServer.serverRegistry.insert(e.player.getDisplayName(), skinBytes);

                    TabFaces.debug("Sending push skin packet to all connected users");
                    for (Object o : FMLCommonHandler.instance()
                        .getMinecraftServerInstance()
                        .getConfigurationManager().playerEntityList) {
                        if (((EntityPlayerMP) o).getDisplayName()
                            .equals(displayName)) {
                            continue;
                        }
                        IMessage msg = new PushSkinbytesToClientPacket.SimpleMessage(
                            e.player.getDisplayName(),
                            skinBytes);
                        PacketHandler.net.sendTo(msg, (EntityPlayerMP) o);
                    }
                }
            }
        }

        DownloadThread dt = new DownloadThread();
        dt.start(skinUrl, e.player.getDisplayName());

        // IMessage msg = new
        // PushAllSkinDataToClient.SimpleMessage(TabFaces.varInstanceServer.serverRegistry.getAllDataAsBytes());
        // PacketHandler.net.sendTo(msg, (EntityPlayerMP) e.player);

        for (String displayName : TabFaces.varInstanceServer.serverRegistry.getAllUsernames()) {
            IMessage msg = new PushSkinbytesToClientPacket.SimpleMessage(
                e.player.getDisplayName(),
                TabFaces.varInstanceServer.serverRegistry.getDataByDisplayName(displayName)
                    .getSkinBytes());
            PacketHandler.net.sendTo(msg, (EntityPlayerMP) e.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLEave(PlayerEvent.PlayerLoggedOutEvent e) {
        TabFaces.varInstanceServer.serverRegistry.removeByDisplayName(e.player.getDisplayName());
    }
}
