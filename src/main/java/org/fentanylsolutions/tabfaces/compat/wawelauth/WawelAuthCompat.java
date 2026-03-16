package org.fentanylsolutions.tabfaces.compat.wawelauth;

import java.util.UUID;

import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.wawelauth.api.WawelFaceRendererClient;
import org.fentanylsolutions.wawelauth.api.WawelTextureResolver;
import org.fentanylsolutions.wawelauth.api.internal.TextureRequest;
import org.fentanylsolutions.wawelauth.wawelclient.WawelClient;

public class WawelAuthCompat {

    public static ResourceLocation getSkin(UUID uuid, String displayName) {
        WawelClient client = WawelClient.instance();
        if (client == null) {
            return null;
        }

        try {
            return client.getTextureResolver()
                .getSkin(uuid, displayName, TextureRequest.DEFAULT);
        } catch (Exception e) {
            TabFaces.debug("WawelAuth skin lookup failed for " + displayName + ": " + e.getMessage());
            return null;
        }
    }

    public static boolean isPlaceholder(ResourceLocation resourceLocation) {
        return resourceLocation == null || WawelTextureResolver.getDefaultSkin()
            .equals(resourceLocation);
    }

    public static void drawFace(ResourceLocation resourceLocation, float xPos, float yPos, float alpha) {
        WawelFaceRendererClient.drawFace(resourceLocation, xPos, yPos, alpha);
    }
}
