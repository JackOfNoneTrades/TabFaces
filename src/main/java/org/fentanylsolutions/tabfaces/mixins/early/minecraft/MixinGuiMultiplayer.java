package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.access.IMixinGuiMultiplayer;
import org.fentanylsolutions.tabfaces.util.ClientUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.authlib.GameProfile;

@SuppressWarnings("unused")
@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends GuiScreen implements IMixinGuiMultiplayer {

    volatile public GameProfile[] visibleInfo;

    /**
     * Redirects the call to func_146283_a inside drawScreen to call our own method instead.
     */
    @SuppressWarnings("unused")
    @Redirect(
        method = "drawScreen",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiMultiplayer;func_146283_a(Ljava/util/List;II)V"))
    private void redirectTooltipRendering(GuiMultiplayer instance, List<String> textLines, int x, int y) {
        myCustomTooltipRenderer(textLines, x, y);
    }

    @Override
    public void setVisiblePlayers(GameProfile[] players) {
        visibleInfo = players;
    }

    @Override
    public GameProfile[] getVisiblePlayers() {
        return visibleInfo;
    }

    private void myCustomTooltipRenderer(List<String> lines, int mouseX, int mouseY) {
        if (Config.enableFacesInServerMenu) {
            List<GameProfile> profileList = new ArrayList<>();

            for (String line : lines) {
                if (TabFaces.varInstanceClient.playerProfileRegistry.hasProfile(line)) {
                    GameProfile profile = TabFaces.varInstanceClient.playerProfileRegistry.getProfile(line);
                    profileList.add(profile);
                }
            }

            GameProfile[] profiles = profileList.toArray(new GameProfile[0]);
            ClientUtil.drawHoveringTextWithFaces(this, profiles, lines, mouseX, mouseY);
        } else {
            this.drawHoveringText(lines, mouseX, mouseY, this.fontRendererObj);
        }
    }
}
