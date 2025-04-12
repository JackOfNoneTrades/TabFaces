package org.fentanylsolutions.tabfaces.util;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.access.IMixinGui;
import org.fentanylsolutions.tabfaces.access.IMixinGuiScreen;
import org.fentanylsolutions.tabfaces.registries.ClientRegistry;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import cpw.mods.fml.common.FMLCommonHandler;

public class ClientUtil {

    public static FontRenderer fontRenderer = null;
    public static int faceWidth = 10;
    public static int serverGuiTTL = 30;
    public static final String fakePlayerUUIDString = "deadbeef-dead-dead-dead-deadbeefdead";
    public static final UUID fakePlayerUUID = UUID.fromString(fakePlayerUUIDString);
    public static final Minecraft minecraftInstance = Minecraft.getMinecraft();

    public static ResourceLocation skinResourceLocation(GameProfile profile) {
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> resultMap = VarInstanceClient.minecraftRef
            .func_152342_ad()
            .func_152788_a(profile);
        if (resultMap.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            ResourceLocation location = VarInstanceClient.minecraftRef.func_152342_ad()
                .func_152792_a(resultMap.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            TabFaces.debug(location.toString());
            return location;
        }
        return Config.showQuestionMarkIfUnknown ? TabFaces.varInstanceClient.defaultResourceLocation
            : AbstractClientPlayer.locationStevePng;
    }

    public static boolean onServer() {
        Minecraft mc = Minecraft.getMinecraft();
        return !mc.isSingleplayer() && !mc.isIntegratedServerRunning() && mc.thePlayer != null;
    }

    public static boolean isOp(EntityPlayerMP entityPlayerMP) {
        // func_152596_g: canSendCommands
        return FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .getConfigurationManager()
            .func_152596_g(entityPlayerMP.getGameProfile());
    }

    public static void drawHoveringTextWithFaces(GuiScreen screen, GameProfile[] profiles, List<String> textLines,
        int x, int y) {
        if (!textLines.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int boxWidth = 0;

            for (String line : textLines) {
                int tmpWidth = fontRenderer.getStringWidth(line);

                if (tmpWidth > boxWidth) {
                    boxWidth = tmpWidth;
                }
            }

            int boxOffsetX = x + 12;
            int boxOffsetY = y - 12;
            int i1 = 8;

            if (textLines.size() > 1) {
                i1 += 2 + (textLines.size() - 1) * 10;
            }

            if (boxOffsetX + boxWidth > screen.width) {
                boxOffsetX -= 28 + boxWidth;
            }

            if (boxOffsetY + i1 + 6 > screen.height) {
                boxOffsetY = screen.height - i1 - 6;
            }

            if (profiles != null) {
                for (String line : textLines) {
                    ClientRegistry.Data data = TabFaces.varInstanceClient.clientRegistry.getByDisplayName(line);
                    if (data != null && data.foundRealSkin) {
                        int tmpWidth = fontRenderer.getStringWidth(line) + faceWidth;
                        if (tmpWidth > boxWidth) {
                            boxWidth = tmpWidth;
                        }
                    }
                }
            }

            ((IMixinGui) screen).setZLevel(300.0F);
            ((IMixinGuiScreen) screen).getItemRender().zLevel = 300.0F;
            int outerBorderColor = -267386864;
            /* outer border */
            ((IMixinGui) screen).drawGradientRectPub(
                boxOffsetX - 3,
                boxOffsetY - 4,
                boxOffsetX + boxWidth + 3,
                boxOffsetY - 3,
                outerBorderColor,
                outerBorderColor); // top
            ((IMixinGui) screen).drawGradientRectPub(
                boxOffsetX - 3,
                boxOffsetY + i1 + 3,
                boxOffsetX + boxWidth + 3,
                boxOffsetY + i1 + 4,
                outerBorderColor,
                outerBorderColor); // bottom
            ((IMixinGui) screen).drawGradientRectPub(
                boxOffsetX - 4,
                boxOffsetY - 3,
                boxOffsetX - 3,
                boxOffsetY + i1 + 3,
                outerBorderColor,
                outerBorderColor); // left
            ((IMixinGui) screen).drawGradientRectPub(
                boxOffsetX + boxWidth + 3,
                boxOffsetY - 3,
                boxOffsetX + boxWidth + 4,
                boxOffsetY + i1 + 3,
                outerBorderColor,
                outerBorderColor); // right
            /* Inner box */
            ((IMixinGui) screen).drawGradientRectPub(
                boxOffsetX - 3,
                boxOffsetY - 3,
                boxOffsetX + boxWidth + 3,
                boxOffsetY + i1 + 3,
                outerBorderColor,
                outerBorderColor);
            int innerBorderColor = 1347420415;
            int l1 = (innerBorderColor & 16711422) >> 1 | innerBorderColor & -16777216;
            /* inner border */
            ((IMixinGui) screen).drawGradientRectPub(
                boxOffsetX - 3,
                boxOffsetY - 3 + 1,
                boxOffsetX - 3 + 1,
                boxOffsetY + i1 + 3 - 1,
                innerBorderColor,
                l1); // left
            // border
            ((IMixinGui) screen).drawGradientRectPub(
                boxOffsetX + boxWidth + 2,
                boxOffsetY - 3 + 1,
                boxOffsetX + boxWidth + 3,
                boxOffsetY + i1 + 3 - 1,
                innerBorderColor,
                l1); // right border
            ((IMixinGui) screen).drawGradientRectPub(
                boxOffsetX - 3,
                boxOffsetY - 3,
                boxOffsetX + boxWidth + 3,
                boxOffsetY - 3 + 1,
                innerBorderColor,
                innerBorderColor); // upper
            // border
            ((IMixinGui) screen).drawGradientRectPub(
                boxOffsetX - 3,
                boxOffsetY + i1 + 2,
                boxOffsetX + boxWidth + 3,
                boxOffsetY + i1 + 3,
                l1,
                l1); // bottom
            // border

            for (int i2 = 0; i2 < textLines.size(); ++i2) {
                String s1 = textLines.get(i2);
                boolean fake = true;
                if (profiles != null) {
                    for (GameProfile profile : profiles) {
                        if (!profile.getId()
                            .equals(ClientUtil.fakePlayerUUID) && profile.getName()
                                .equals(s1)) {

                            if (!TabFaces.varInstanceClient.clientRegistry.displayNameInRegistry(s1)) {
                                TabFaces.varInstanceClient.clientRegistry
                                    .insert(s1, profile.getId(), null, true, serverGuiTTL);
                            }

                            ResourceLocation rl = TabFaces.varInstanceClient.clientRegistry
                                .getTabMenuResourceLocation(s1, true, serverGuiTTL);

                            ClientRegistry.Data data = TabFaces.varInstanceClient.clientRegistry
                                .getByDisplayName(profile.getName());
                            if (data == null || !data.foundRealSkin) {
                                rl = null;
                                fontRenderer.drawStringWithShadow(s1, boxOffsetX, boxOffsetY, -1);
                            } else {
                                fontRenderer.drawStringWithShadow(s1, boxOffsetX + faceWidth, boxOffsetY, -1);
                            }

                            if (rl != null) {
                                drawPlayerFace(rl, boxOffsetX, boxOffsetY, 1.0f);
                            }
                            fake = false;
                            break;
                        }
                    }
                }
                if (fake) {
                    fontRenderer.drawStringWithShadow(s1, boxOffsetX, boxOffsetY, -1);
                }

                if (i2 == 0) {
                    boxOffsetY += 2;
                }

                boxOffsetY += 10;
            }

            ((IMixinGui) screen).setZLevel(0.0F);
            ((IMixinGuiScreen) screen).getItemRender().zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    public static void drawPlayerFace(String displayName, int xPos, int yPos, float alpha) {
        ResourceLocation rl = TabFaces.varInstanceClient.clientRegistry
            .getTabMenuResourceLocation(displayName, false, -1);
        if (rl != null) {
            drawPlayerFace(rl, xPos, yPos, alpha);
        }
    }

    public static void drawPlayerFace(ResourceLocation rl, float xPos, float yPos, float alpha) {
        if (rl != null) {
            VarInstanceClient.minecraftRef.getTextureManager()
                .bindTexture(rl);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            // int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float
            // tileWidth, float tileHeight
            drawTexFloat(xPos, yPos, 8, 14, 8, 18, 8, 8, 64.0F, 64.0F);
        }
    }

    public static void drawTexFloat(float x, float y, float u, float v, int uWidth, int vHeight, int width, int height,
        float tileWidth, float tileHeight) {
        float f4 = 1.0F / tileWidth;
        float f5 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0.0D, u * f4, (v + (float) vHeight) * f5);
        tessellator.addVertexWithUV(x + width, y + height, 0.0D, (u + (float) uWidth) * f4, (v + (float) vHeight) * f5);
        tessellator.addVertexWithUV(x + width, y, 0.0D, (u + (float) uWidth) * f4, v * f5);
        tessellator.addVertexWithUV(x, y, 0.0D, u * f4, v * f5);
        tessellator.draw();
    }
}
