package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.util.ServerDebugUuidProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

@Mixin(ServerConfigurationManager.class)
public abstract class MixinServerConfigurationManager {

    private static final String TEAM_NAME = "tf_dbg";

    @Shadow
    @Final
    private MinecraftServer mcServer;

    @Inject(method = "playerLoggedIn", at = @At("TAIL"))
    private void tabfaces$injectDebugTabListEntries(EntityPlayerMP player, CallbackInfo ci) {
        if (!Config.debugInjectServerStatusProfiles) {
            return;
        }

        GameProfile[] debugProfiles = ServerDebugUuidProvider.getDebugProfiles();
        if (debugProfiles.length == 0) {
            return;
        }

        int maxProfiles = Math.min(Config.debugInjectServerStatusProfilesCount, debugProfiles.length);
        for (int i = 0; i < maxProfiles; i++) {
            String name = debugProfiles[i].getName();
            player.playerNetServerHandler.sendPacket(new S38PacketPlayerListItem(name, true, 0));
        }

        TabFaces.info("Injected " + maxProfiles + " debug players into tab list for " + player.getCommandSenderName());

        String prefix = Config.debugTabListPrefix;
        if (prefix != null && !prefix.isEmpty()) {
            Scoreboard scoreboard = mcServer.worldServerForDimension(0)
                .getScoreboard();
            ScorePlayerTeam team = scoreboard.getTeam(TEAM_NAME);
            if (team == null) {
                team = scoreboard.createTeam(TEAM_NAME);
                team.setNamePrefix(prefix);
                TabFaces.info("Created debug scoreboard team with prefix: " + prefix);
            }

            scoreboard.func_151392_a(player.getCommandSenderName(), TEAM_NAME);

            for (int i = 0; i < maxProfiles; i++) {
                scoreboard.func_151392_a(debugProfiles[i].getName(), TEAM_NAME);
            }
        }
    }
}
