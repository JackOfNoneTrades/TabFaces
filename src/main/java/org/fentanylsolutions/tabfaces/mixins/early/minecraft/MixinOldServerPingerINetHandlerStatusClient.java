package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.util.IChatComponent;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.access.IMixinServerData;
import org.fentanylsolutions.tabfaces.util.PingHandlerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.authlib.GameProfile;

@SuppressWarnings("unused")
@Mixin(targets = "net.minecraft.client.network.OldServerPinger$1")
public abstract class MixinOldServerPingerINetHandlerStatusClient implements INetHandlerStatusClient {

    @Inject(method = "handleServerInfo", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onServerInfo(S00PacketServerInfo packet, CallbackInfo ci, ServerStatusResponse response) {
        if (response == null || response.func_151318_b() == null) return;

        GameProfile[] profiles = response.func_151318_b()
            .func_151331_c();
        if (profiles != null) {
            TabFaces.debug("GOT PROFILES");
            for (GameProfile gameprofile : profiles) {
                TabFaces.debug(gameprofile.getName() + ":" + gameprofile.getId());
            }

            ServerData serverData = PingHandlerContext.get(this);
            if (serverData != null) {
                ((IMixinServerData) serverData).setProfiles(profiles);
                TabFaces.debug("Associated server " + serverData.serverIP + " with profiles of len " + profiles.length);
            }
        }

        TabFaces.debug("Hooked into onHandleServerInfo");
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(IChatComponent reason, CallbackInfo ci) {
        PingHandlerContext.remove(this);
    }
}
