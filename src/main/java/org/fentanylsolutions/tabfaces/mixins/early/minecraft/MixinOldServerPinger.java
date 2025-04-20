package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.access.IMixinOldServerPinger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* We can't use an inject because INetHandler is constructed as an arg */
@SuppressWarnings("unused")
@Mixin(OldServerPinger.class)
public class MixinOldServerPinger implements IMixinOldServerPinger {

    @Unique
    String serverIp;

    @Inject(
        method = "func_147224_a",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/NetworkManager;setNetHandler(Lnet/minecraft/network/INetHandler;)V"))
    private void onBeforeSetNetHandler(ServerData server, CallbackInfo ci) {
        TabFaces.debug(server.serverIP);

        setIp(server.serverIP);
    }

    @Override
    public String getIp() {
        return this.serverIp;
    }

    @Override
    public void setIp(String serverIp) {
        this.serverIp = serverIp;
    }
}
