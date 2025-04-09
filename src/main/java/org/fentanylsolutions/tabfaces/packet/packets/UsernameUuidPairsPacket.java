package org.fentanylsolutions.tabfaces.packet.packets;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.fentanylsolutions.tabfaces.TabFaces;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class UsernameUuidPairsPacket implements IMessageHandler<UsernameUuidPairsPacket.Message, IMessage> {

    @Override
    public IMessage onMessage(Message message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            TabFaces.debug("Received Username:UUID packet with " + message.pairs.size() + " entries");
            for (UsernameUuidPair pair : message.pairs) {
                TabFaces.debug(" -> " + pair.username + " : " + pair.uuid);
                TabFaces.varInstanceClient.clientRegistry.insert(pair.username, UUID.fromString(pair.uuid), null);
            }
        }
        return null;
    }

    public static class UsernameUuidPair {

        public final String username;
        public final String uuid;

        public UsernameUuidPair(String username, String uuid) {
            this.username = username;
            this.uuid = uuid;
        }
    }

    public static class Message implements IMessage {

        public List<UsernameUuidPair> pairs = new ArrayList<>();

        @SuppressWarnings("unused")
        public Message() {}

        public Message(List<UsernameUuidPair> pairs) {
            this.pairs = pairs;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            int pairCount = buf.readInt();
            for (int i = 0; i < pairCount; i++) {
                String username = readString(buf);
                String uuid = readString(buf);
                pairs.add(new UsernameUuidPair(username, uuid));
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(pairs.size());
            for (UsernameUuidPair pair : pairs) {
                writeString(buf, pair.username);
                writeString(buf, pair.uuid);
            }
        }

        private String readString(ByteBuf buf) {
            int length = buf.readInt();
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        private void writeString(ByteBuf buf, String str) {
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        }
    }
}
