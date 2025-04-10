package org.fentanylsolutions.tabfaces.util;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.access.IMixinGui;
import org.fentanylsolutions.tabfaces.access.IMixinGuiScreen;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class Util {

    public static FontRenderer fontRenderer = null;
    public static int faceWidth = 10;
    public static int serverGuiTTL = 30;
    public static final String fakePlayerUUIDString = "deadbeef-dead-dead-dead-deadbeefdead";
    public static final UUID fakePlayerUUID = UUID.fromString(fakePlayerUUIDString);
    public static final Minecraft minecraftInstance = Minecraft.getMinecraft();

    public static boolean isServer() {
        return FMLCommonHandler.instance()
            .getSide() == Side.SERVER;
    }

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

    /* This function should be called from a thread, as it makes a sync network call */
    public static GameProfile getFullProfile(UUID id, String displayName) {
        GameProfile profile = new GameProfile(id, displayName);
        try {
            return VarInstanceClient.sessionService.fillProfileProperties(profile, true);
        } catch (Exception e) {
            TabFaces.error("Failed to get profile for " + displayName + ":" + id.toString());
        }
        return null;
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
            int k = 0;

            for (String s : textLines) {
                int l = fontRenderer.getStringWidth(s);

                if (l > k) {
                    k = l;
                }
            }

            int j2 = x + 12;
            int k2 = y - 12;
            int i1 = 8;

            if (textLines.size() > 1) {
                i1 += 2 + (textLines.size() - 1) * 10;
            }

            if (j2 + k > screen.width) {
                j2 -= 28 + k;
            }

            if (k2 + i1 + 6 > screen.height) {
                k2 = screen.height - i1 - 6;
            }

            int finalAddedWidth = 0;
            int longestRealName = 0;
            int longestFakeName = 0;
            if (profiles != null) {
                for (String line : textLines) {
                    for (GameProfile profile : profiles) {
                        if (line.equals(profile.getName())) {
                            int strWidth = fontRenderer.getStringWidth(profile.getName());
                            if (!profile.getId()
                                .equals(Util.fakePlayerUUID)) {
                                if (strWidth > longestRealName) {
                                    longestRealName = strWidth;
                                }
                            } else {
                                if (fontRenderer.getStringWidth(profile.getName()) > longestFakeName) {
                                    longestFakeName = strWidth;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            if (longestRealName > longestFakeName) {
                finalAddedWidth = 10;
            } else if (longestFakeName - longestRealName < faceWidth) {
                finalAddedWidth = faceWidth - (longestFakeName - longestRealName);
            }

            ((IMixinGui) screen).setZLevel(300.0F);
            ((IMixinGuiScreen) screen).getItemRender().zLevel = 300.0F;
            int j1 = -267386864;
            /* outer border */
            ((IMixinGui) screen).drawGradientRectPub(j2 - 3, k2 - 4, j2 + k + 3 + finalAddedWidth, k2 - 3, j1, j1); // top
            ((IMixinGui) screen)
                .drawGradientRectPub(j2 - 3, k2 + i1 + 3, j2 + k + 3 + finalAddedWidth, k2 + i1 + 4, j1, j1); // bottom
            ((IMixinGui) screen).drawGradientRectPub(j2 - 4, k2 - 3, j2 - 3 + finalAddedWidth, k2 + i1 + 3, j1, j1); // left
            ((IMixinGui) screen).drawGradientRectPub(
                j2 + k + 3 + finalAddedWidth,
                k2 - 3,
                j2 + k + 4 + finalAddedWidth,
                k2 + i1 + 3,
                j1,
                j1); // right
            /* Inner box */
            ((IMixinGui) screen).drawGradientRectPub(j2 - 3, k2 - 3, j2 + k + 3 + finalAddedWidth, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            /* inner border */
            ((IMixinGui) screen).drawGradientRectPub(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1); // left
                                                                                                               // border
            ((IMixinGui) screen).drawGradientRectPub(
                j2 + k + 2 + finalAddedWidth,
                k2 - 3 + 1,
                j2 + k + 3 + finalAddedWidth,
                k2 + i1 + 3 - 1,
                k1,
                l1); // right border
            ((IMixinGui) screen).drawGradientRectPub(j2 - 3, k2 - 3, j2 + k + 3 + finalAddedWidth, k2 - 3 + 1, k1, k1); // upper
                                                                                                                        // border
            ((IMixinGui) screen)
                .drawGradientRectPub(j2 - 3, k2 + i1 + 2, j2 + k + 3 + finalAddedWidth, k2 + i1 + 3, l1, l1); // bottom
                                                                                                              // border

            for (int i2 = 0; i2 < textLines.size(); ++i2) {
                String s1 = textLines.get(i2);
                boolean fake = true;
                if (profiles != null) {
                    for (GameProfile profile : profiles) {
                        if (!profile.getId()
                            .equals(Util.fakePlayerUUID) && profile.getName()
                                .equals(s1)) {
                            fontRenderer.drawStringWithShadow(s1, j2 + faceWidth, k2, -1);

                            if (!TabFaces.varInstanceClient.clientRegistry.displayNameInRegistry(s1)) {
                                TabFaces.varInstanceClient.clientRegistry
                                    .insert(s1, profile.getId(), null, true, serverGuiTTL);
                            }

                            ResourceLocation rl = TabFaces.varInstanceClient.clientRegistry
                                .getTabMenuResourceLocation(s1, true, serverGuiTTL);

                            if (rl != null) {
                                minecraftInstance.getTextureManager()
                                    .bindTexture(rl);
                                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                                // int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float
                                // tileWidth, float tileHeight
                                Gui.func_152125_a(j2, k2, 8, 14, 8, 18, 8, 8, 64.0F, 64.0F);

                            }
                            fake = false;
                            break;
                        }
                    }
                }
                if (fake) {
                    fontRenderer.drawStringWithShadow(s1, j2, k2, -1);
                }

                if (i2 == 0) {
                    k2 += 2;
                }

                k2 += 10;
            }

            ((IMixinGui) screen).setZLevel(0.0F);
            ((IMixinGuiScreen) screen).getItemRender().zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }
}
