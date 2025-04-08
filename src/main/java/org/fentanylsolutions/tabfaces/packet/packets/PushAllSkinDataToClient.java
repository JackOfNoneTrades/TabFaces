package org.fentanylsolutions.tabfaces.packet.packets;

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

public class PushAllSkinDataToClient implements IMessageHandler<PushAllSkinDataToClient.SimpleMessage, IMessage> {

    @Override
    public IMessage onMessage(SimpleMessage message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            TabFaces.debug("Received PushAllSkinDataToClient packet");

            /* Clearing client registry */
            TabFaces.varInstanceClient.clientRegistry.clear();

            if (message.dataBytes == null) {
                TabFaces.error("Received message.dataBytes are null");
                return null;
            }
            if (message.dataBytes.length == 5) {
                TabFaces.info("message.bytes len is 5, no data");
                return null;
            }

            /* Stripping leading byte (forge adds one at the beginning) */
            message.dataBytes = Util.removeXLeadingBytes(1, message.dataBytes);

            int amountOfData = Util.fourFirstBytesToInt(message.dataBytes);
            message.dataBytes = Util.removeFourLeadingBytes(message.dataBytes);

            for (int i = 0; i < amountOfData; i++) {
                int displayNameLen = Util.fourFirstBytesToInt(message.dataBytes);
                if (displayNameLen > 20) {
                    message.dataBytes = Util.removeXLeadingBytes(displayNameLen, message.dataBytes);
                    TabFaces.error("Username bytes longer than 20");
                    continue;
                }
                message.dataBytes = Util.removeFourLeadingBytes(message.dataBytes);
                String displayName = new String(
                    Util.getXLeadingBytes(displayNameLen, message.dataBytes),
                    Charsets.UTF_8);
                message.dataBytes = Util.removeXLeadingBytes(displayNameLen, message.dataBytes);

                int skinByteLen = Util.fourFirstBytesToInt(message.dataBytes);
                message.dataBytes = Util.removeFourLeadingBytes(message.dataBytes);
                if (skinByteLen > Config.maxAcceptedSkinBytes) {
                    message.dataBytes = Util.removeXLeadingBytes(skinByteLen, message.dataBytes);
                    TabFaces.error(
                        "Amount of skin bytes (" + skinByteLen
                            + ") larger than set in config ("
                            + Config.maxAcceptedSkinBytes
                            + ")");
                    return null;
                }
                byte[] skinBytes = Util.getXLeadingBytes(skinByteLen, message.dataBytes);
                message.dataBytes = Util.removeXLeadingBytes(skinByteLen, message.dataBytes);
                if (!Util.pngIsSane(skinBytes)) {
                    TabFaces.error("Skin image is not sane!: " + displayName);
                    return null;
                }
                ResourceLocation rl = ClientUtil.loadSkinFromBytes(skinBytes, displayName);
                TabFaces.varInstanceClient.clientRegistry.setTabMenuResourceLocation(displayName, rl);
            }
        }
        return null;
    }

    public static class SimpleMessage implements IMessage {

        private byte[] dataBytes;

        // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        public SimpleMessage() {}

        public SimpleMessage(byte[] dataBytes) {
            this.dataBytes = dataBytes;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.dataBytes = buf.array();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeBytes(this.dataBytes);
        }
    }
}
