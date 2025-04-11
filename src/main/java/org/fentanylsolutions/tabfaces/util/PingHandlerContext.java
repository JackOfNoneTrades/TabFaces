package org.fentanylsolutions.tabfaces.util;

import java.util.WeakHashMap;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.INetHandler;

public class PingHandlerContext {

    private static final WeakHashMap<INetHandler, ServerData> handlerMap = new WeakHashMap<>();

    public static void associate(INetHandler handler, ServerData server) {
        handlerMap.put(handler, server);
    }

    public static ServerData get(INetHandler handler) {
        return handlerMap.get(handler);
    }

    public static void remove(INetHandler handler) {
        handlerMap.remove(handler);
    }
}
