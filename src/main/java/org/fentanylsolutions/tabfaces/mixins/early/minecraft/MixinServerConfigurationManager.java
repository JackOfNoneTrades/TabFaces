package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.server.management.ServerConfigurationManager;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.util.ServerDebugUuidProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

@Mixin(ServerConfigurationManager.class)
public abstract class MixinServerConfigurationManager {

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
    }
}
