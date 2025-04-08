package org.fentanylsolutions.tabfaces.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.TabFaces;

public class ClientUtil {

    public static class OfflineTextureObject extends AbstractTexture {

        private final BufferedImage image;

        public OfflineTextureObject(BufferedImage image) {
            this.image = image;
        }

        public BufferedImage getImage() {
            return image;
        }

        @Override
        public void loadTexture(IResourceManager arg0) {
            deleteGlTexture();

            TextureUtil.uploadTextureImageAllocate(getGlTextureId(), image, false, false);
        }

    }

    public static void loadTexture(BufferedImage bufferedImage, ResourceLocation resourceLocation) {
        if (bufferedImage == null || resourceLocation == null) {
            TabFaces.error("Error loading texture!");
            return;
        }
        OfflineTextureObject offlineTextureObject = new OfflineTextureObject(bufferedImage);
        Minecraft.getMinecraft()
            .getTextureManager()
            .loadTexture(resourceLocation, offlineTextureObject);
    }

    public static ResourceLocation loadSkinFromBytes(byte[] skinBytes, String displayName) {
        if (skinBytes == null || skinBytes.length == 0) {
            TabFaces.error("Invalid skinBytes");
            return null;
        }
        if (displayName == null || displayName.length() == 0) {
            TabFaces.error("Invalid displayName");
        }

        BufferedImage im = bufferedImageFromBytes(skinBytes);
        if (im == null) {
            TabFaces.error("Failed to load skin from bytes, im null");
            return null;
        }
        if (im.getHeight() == 64) {
            im = new LegacyConversion().convert(im);
        }
        ResourceLocation rl = new ResourceLocation("tabfaces", "tabfaces/" + displayName);
        loadTexture(im, rl);
        return rl;
    }

    public static BufferedImage bufferedImageFromBytes(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);
        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            TabFaces.error("Failed to read skin bytes into bufferedimage");
            e.printStackTrace();
        }
        return null;
    }
}
