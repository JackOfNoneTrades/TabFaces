package org.fentanylsolutions.tabfaces.util;

import net.minecraft.client.multiplayer.ServerData;

public class ServerDataHook {

    public static final ThreadLocal<ServerData> CURRENT_SERVER = new ThreadLocal<>();
}
