package org.fentanylsolutions.tabfaces.varinstances;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.registries.ClientRegistry;
import org.fentanylsolutions.tabfaces.registries.ServerDataRegistry;

import com.mojang.authlib.minecraft.MinecraftSessionService;

public class VarInstanceClient {

    public ClientRegistry clientRegistry = new ClientRegistry();
    public ServerDataRegistry serverDataRegistry = new ServerDataRegistry();
    /*
     * public int u = 0;
     * public int v = 0;
     * public int uWidth = 8;
     * public int vHeight = 8;
     */
    public ResourceLocation defaultResourceLocation = new ResourceLocation(TabFaces.MODID, "textures/default.png");
    public static Minecraft minecraftRef = Minecraft.getMinecraft();
    public static MinecraftSessionService sessionService = minecraftRef.func_152347_ac();
}
