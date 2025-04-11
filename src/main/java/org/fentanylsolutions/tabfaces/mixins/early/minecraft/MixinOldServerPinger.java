package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;

import org.fentanylsolutions.tabfaces.util.PingHandlerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OldServerPinger.class)
public class MixinOldServerPinger {

    /*
     * @Inject(method = "func_147224_a", at = @At("HEAD"))
     * private void onPingStart(ServerData server, CallbackInfo ci) {
     * if (server != null) {
     * //TabFaces.varInstanceClient.pingingServers.put(server.serverIP, server);
     * ServerPingerContext.set(server);
     * TabFaces.debug("Added server " + server.serverName + " to pingingServers map with IP: " + server.serverIP);
     * } else {
     * TabFaces.debug("Got null server in onPingStart");
     * }
     * }
     */
    @Redirect(
        method = "func_147224_a",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/NetworkManager;setNetHandler(Lnet/minecraft/network/INetHandler;)V"))
    private void interceptSetNetHandler(NetworkManager manager, INetHandler handler, ServerData server) {
        PingHandlerContext.associate(handler, server);
        manager.setNetHandler(handler); // Forward the call
    }
}
