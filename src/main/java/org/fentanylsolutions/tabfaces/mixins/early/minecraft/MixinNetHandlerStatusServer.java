package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusServer;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerStatusServer;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.util.ServerDebugUuidProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

@Mixin(NetHandlerStatusServer.class)
public abstract class MixinNetHandlerStatusServer implements INetHandlerStatusServer {

    @Shadow
    @Final
    private MinecraftServer field_147314_a;

    @Inject(method = "processServerQuery", at = @At("HEAD"))
    private void tabfaces$injectDebugProfiles(C00PacketServerQuery packetIn, CallbackInfo ci) {
        if (!Config.debugInjectServerStatusProfiles) {
            return;
        }

        GameProfile[] debugProfiles = ServerDebugUuidProvider.getDebugProfiles();
        if (debugProfiles.length == 0) {
            return;
        }
        int maxProfiles = Math.min(Config.debugInjectServerStatusProfilesCount, debugProfiles.length);
        GameProfile[] injectedProfiles = new GameProfile[maxProfiles];
        System.arraycopy(debugProfiles, 0, injectedProfiles, 0, maxProfiles);

        ServerStatusResponse status = this.field_147314_a.func_147134_at();
        if (status == null) {
            return;
        }

        ServerStatusResponse.PlayerCountData playerData = status.func_151318_b();
        if (playerData == null) {
            playerData = new ServerStatusResponse.PlayerCountData(
                this.field_147314_a.getMaxPlayers(),
                this.field_147314_a.getCurrentPlayerCount());
            status.func_151319_a(playerData);
        }

        playerData.func_151330_a(injectedProfiles);
    }
}
