package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.server.S00PacketServerInfo;

import org.fentanylsolutions.tabfaces.TabFaces;
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
        TabFaces.debug(
            "Got server info response " + (response != null ? response.func_151317_a()
                .getUnformattedText() : "null"));
        if (response == null) {
            return;
        }

        GameProfile[] profiles = response.func_151318_b()
            .func_151331_c();

        if (profiles != null) {
            TabFaces.debug("GOT PROFILES FOR (" + profiles.length + ")");
            for (GameProfile gameprofile : profiles) {
                // TabFaces.debug(gameprofile.getName() + ":" + gameprofile.getId());
                TabFaces.varInstanceClient.playerProfileRegistry.setProfile(gameprofile.getName(), gameprofile);
            }
        } else {
            TabFaces.error("Didn't get any profiles");
        }

        TabFaces.debug("Hooked into onHandleServerInfo");
    }
}
