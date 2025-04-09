package org.fentanylsolutions.tabfaces.packet.packets;

import java.nio.charset.StandardCharsets;

import org.fentanylsolutions.tabfaces.TabFaces;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketRemoveFromCache implements IMessageHandler<PacketRemoveFromCache.Message, IMessage> {

    @Override
    public IMessage onMessage(Message message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            TabFaces.debug("Received PacketRemoveFromCache for displayName: " + message.displayName);
            TabFaces.varInstanceClient.clientRegistry.removeByDisplayName(message.displayName);
        }
        return null;
    }

    public static class Message implements IMessage {

        private String displayName;

        @SuppressWarnings("unused")
        public Message() {}

        public Message(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            int length = buf.readInt();
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            displayName = new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            byte[] bytes = displayName.getBytes(StandardCharsets.UTF_8);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        }
    }
}
