package org.fentanylsolutions.tabfaces.packet.packets;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.util.ClientUtil;
import org.fentanylsolutions.tabfaces.util.Util;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PushSkinbytesToClientPacket
    implements IMessageHandler<PushSkinbytesToClientPacket.SimpleMessage, IMessage> {

    @Override
    public IMessage onMessage(SimpleMessage message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            TabFaces.debug("Received PushSkinbytesToClientPacket packet");
            if (message.resourceBytes == null) {
                TabFaces.debug("Received message.resourceBytes are null");
                return null;
            }
            if (message.resourceBytes.length > Config.maxAcceptedSkinBytes) {
                TabFaces.error(
                    "Amount of skin bytes (" + message.resourceBytes.length
                        + ") larger than set in config ("
                        + Config.maxAcceptedSkinBytes
                        + ")");
                return null;
            }
            if (!Util.pngIsSane(message.resourceBytes)) {
                TabFaces.error("Skin image is not sane!: " + message.displayName);
                return null;
            }
            if (message.displayName == null || message.displayName.length() == 0) {
                TabFaces.error("Invalid displayname");
                return null;
            }
            // ClientUtil.saveSkinBytesToCache(message.resourceBytes, message.displayName);
            ResourceLocation rl = ClientUtil.loadSkinFromBytes(message.resourceBytes, message.displayName);
            TabFaces.varInstanceClient.clientRegistry.setTabMenuResourceLocation(message.displayName, rl);
        }
        return null;
    }

    public static class SimpleMessage implements IMessage {

        private String displayName;
        private byte[] resourceBytes;

        // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        public SimpleMessage() {}

        public SimpleMessage(String displayName, byte[] resourceBytes) {
            this.displayName = displayName;
            this.resourceBytes = resourceBytes;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            byte[] receivingData = buf.array();
            byte[] byteDataLenBytes = Arrays.copyOfRange(receivingData, 1, 5);
            int byteDataLen = new BigInteger(byteDataLenBytes).intValue();
            byte[] byteData = Arrays.copyOfRange(receivingData, 9, 9 + byteDataLen);
            this.displayName = new String(byteData, StandardCharsets.UTF_8);
            this.resourceBytes = Arrays.copyOfRange(receivingData, 9 + byteDataLen, receivingData.length);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            try {
                String stringData = this.displayName;
                byte[] byteData = stringData.getBytes(Charsets.UTF_8);
                int byteDataLen = byteData.length;
                byte[] byteDataLenBytes = Util.fillByteArrayLeading(
                    BigInteger.valueOf(byteDataLen)
                        .toByteArray(),
                    4);

                int skinByteLen = this.resourceBytes != null ? this.resourceBytes.length : 0;
                byte[] byteSkinLenBytes = Util.fillByteArrayLeading(
                    BigInteger.valueOf(skinByteLen)
                        .toByteArray(),
                    4);

                byte[] resultingData;
                if (this.resourceBytes != null) {
                    resultingData = Util.concatByteArrays(
                        Util.concatByteArrays(byteDataLenBytes, byteSkinLenBytes),
                        Util.concatByteArrays(byteData, this.resourceBytes));
                } else {
                    resultingData = Util
                        .concatByteArrays(Util.concatByteArrays(byteDataLenBytes, byteSkinLenBytes), byteData);
                }
                buf.writeBytes(resultingData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
