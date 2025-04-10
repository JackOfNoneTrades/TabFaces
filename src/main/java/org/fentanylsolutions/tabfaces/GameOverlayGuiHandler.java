package org.fentanylsolutions.tabfaces;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.fentanylsolutions.tabfaces.util.ClientUtil;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GameOverlayGuiHandler extends GuiIngame {

    public GameOverlayGuiHandler(Minecraft mc) {
        super(mc);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void open(RenderGameOverlayEvent.Pre e) {
        if (!Config.enableFacesInTabMenu) {
            return;
        }
        if (e.type == RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            e.setCanceled(true);
        }

        if (e.type != RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            return;
        }

        ScoreObjective scoreobjective = this.mc.theWorld.getScoreboard()
            .func_96539_a(0);
        NetHandlerPlayClient handler = mc.thePlayer.sendQueue;

        if (mc.gameSettings.keyBindPlayerList.getIsKeyPressed()
            && (!mc.isIntegratedServerRunning() || handler.playerInfoList.size() > 1 || scoreobjective != null)) {
            this.mc.mcProfiler.startSection("playerList");
            List<GuiPlayerInfo> players = handler.playerInfoList;

            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int width = res.getScaledWidth();

            int maxPlayers = handler.currentServerMaxPlayers;
            int rows = maxPlayers;
            int columns = 1;

            for (columns = 1; rows > 20; rows = (maxPlayers + columns - 1) / columns) {
                columns++;
            }

            int columnWidth = 300 / columns;

            if (columnWidth > 150) {
                columnWidth = 150;
            }

            int left = (width - columns * columnWidth) / 2;
            byte border = 10;
            drawRect(left - 1, border - 1, left + columnWidth * columns, border + 9 * rows, Integer.MIN_VALUE);

            for (int i = 0; i < maxPlayers; i++) {
                int xPos = left + i % columns * columnWidth;
                int yPos = border + i / columns * 9;
                drawRect(xPos, yPos, xPos + columnWidth - 1, yPos + 8, 553648127);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_ALPHA_TEST);

                if (i < players.size()) {
                    GuiPlayerInfo player = players.get(i);
                    ScorePlayerTeam team = mc.theWorld.getScoreboard()
                        .getPlayersTeam(player.name);
                    String displayName = ScorePlayerTeam.formatPlayerName(team, player.name);

                    ResourceLocation rl = TabFaces.varInstanceClient.clientRegistry
                        .getTabMenuResourceLocation(player.name, false, -1);

                    if (rl != null) {
                        mc.getTextureManager()
                            .bindTexture(rl);
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        // int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float
                        // tileWidth, float tileHeight
                        Gui.func_152125_a(xPos, yPos, 8, 14, 8, 18, 8, 8, 64.0F, 64.0F);

                    }

                    ClientUtil.fontRenderer.drawStringWithShadow(displayName, xPos + 10, yPos, 16777215);

                    if (scoreobjective != null) {
                        int endX = xPos + ClientUtil.fontRenderer.getStringWidth(displayName) + 5;
                        int maxX = xPos + columnWidth - 12 - 5;

                        if (maxX - endX > 5) {
                            Score score = scoreobjective.getScoreboard()
                                .func_96529_a(player.name, scoreobjective);
                            String scoreDisplay = EnumChatFormatting.YELLOW + "" + score.getScorePoints();
                            ClientUtil.fontRenderer.drawStringWithShadow(
                                scoreDisplay,
                                maxX - ClientUtil.fontRenderer.getStringWidth(scoreDisplay),
                                yPos,
                                16777215);
                        }
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                    mc.getTextureManager()
                        .bindTexture(Gui.icons);
                    int pingIndex = 4;
                    int ping = player.responseTime;
                    if (ping < 0) pingIndex = 5;
                    else if (ping < 150) pingIndex = 0;
                    else if (ping < 300) pingIndex = 1;
                    else if (ping < 600) pingIndex = 2;
                    else if (ping < 1000) pingIndex = 3;

                    zLevel += 100.0F;
                    drawTexturedModalRect(xPos + columnWidth - 12, yPos, 0, 176 + pingIndex * 8, 10, 8);
                    zLevel -= 100.0F;
                }
            }
        }
    }
}
