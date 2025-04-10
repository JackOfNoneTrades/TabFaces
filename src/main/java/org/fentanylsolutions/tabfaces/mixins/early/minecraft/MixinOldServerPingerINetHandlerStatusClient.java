package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.server.S00PacketServerInfo;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.access.IMixinServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.authlib.GameProfile;

@SuppressWarnings("unused")
@Mixin(targets = "net.minecraft.client.network.OldServerPinger$1")
public abstract class MixinOldServerPingerINetHandlerStatusClient implements INetHandlerStatusClient {

    @Shadow(remap = false)
    private ServerData val$server;

    private ServerData getServerData() {
        return this.val$server;
    }

    @Inject(method = "handleServerInfo", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onHandleServerInfo(S00PacketServerInfo packetIn, CallbackInfo ci, ServerStatusResponse response) {
        if (response == null || response.func_151318_b() == null) return;

        GameProfile[] profiles = response.func_151318_b()
            .func_151331_c();
        if (profiles != null) {
            // Access the captured ServerData from the outer scope
            // This field exists due to Java's anonymous class capture
            TabFaces.info("GOT PROFILES");
            int i = profiles.length;

            for (int j = 0; j < i; ++j) {
                GameProfile gameprofile = profiles[j];
                TabFaces.info(gameprofile.getName() + ":" + gameprofile.getId());
            }
            ServerData server = this.getServerData(); // we'll define this below
            TabFaces.info(server.serverIP);
            // server.setVisiblePlayers(profiles);
            ((IMixinServerData) server).setVisiblePlayers(profiles);
            ((IMixinServerData) server).sayLmao();
        }
        TabFaces.debug("Hooked into onHandleServerInfo");
    }
}
