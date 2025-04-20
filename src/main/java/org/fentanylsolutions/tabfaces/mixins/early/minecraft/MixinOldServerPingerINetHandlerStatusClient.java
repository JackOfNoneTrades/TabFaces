package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.server.S00PacketServerInfo;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.access.IMixinOldServerPinger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;

@SuppressWarnings("unused")
@Mixin(targets = "net.minecraft.client.network.OldServerPinger$1")
public abstract class MixinOldServerPingerINetHandlerStatusClient implements INetHandlerStatusClient {

    @Shadow(remap = false)
    @Final
    OldServerPinger this$0;

    @Inject(method = "handleServerInfo", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onServerInfo(S00PacketServerInfo packet, CallbackInfo ci, ServerStatusResponse response,
        @Share("servershare") LocalRef<ServerData> serverDataLocalRef) {
        TabFaces.debug(
            "Got server info response " + (response != null ? response.func_151317_a()
                .getUnformattedText() : "null"));
        TabFaces.debug(this$0.toString());
        if (response == null) {
            return;
        }

        GameProfile[] profiles = response.func_151318_b()
            .func_151331_c();

        if (profiles != null) {
            TabFaces.debug("GOT PROFILES (" + profiles.length + ")");
            for (GameProfile gameprofile : profiles) {
                // TabFaces.debug(gameprofile.getName() + ":" + gameprofile.getId());
            }

            System.out.println();

            String ip = ((IMixinOldServerPinger) this$0).getIp();
            if (ip != null) {
                TabFaces.varInstanceClient.serverDataRegistry.setProfiles(ip, profiles.clone());
            } else {
                TabFaces.error("IP is null");
            }
        } else {
            TabFaces.error("Didn't get any profiles");
        }

        TabFaces.debug("Hooked into onHandleServerInfo");
    }
}
