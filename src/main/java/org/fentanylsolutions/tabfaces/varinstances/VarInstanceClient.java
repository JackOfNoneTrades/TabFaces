package org.fentanylsolutions.tabfaces.varinstances;

import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.registries.ClientRegistry;

public class VarInstanceClient {

    public ClientRegistry clientRegistry = new ClientRegistry();
    /*
     * public int u = 0;
     * public int v = 0;
     * public int uWidth = 8;
     * public int vHeight = 8;
     */
    public ResourceLocation defaultResourceLocation = new ResourceLocation(TabFaces.MODID, "textures/default.png");
}
