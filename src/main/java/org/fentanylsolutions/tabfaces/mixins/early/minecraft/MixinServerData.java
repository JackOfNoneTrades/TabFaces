package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.multiplayer.ServerData;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.access.IMixinServerData;
import org.spongepowered.asm.mixin.Mixin;

import com.mojang.authlib.GameProfile;

@Mixin(ServerData.class)
public class MixinServerData implements IMixinServerData {

    private GameProfile[] visiblePlayers;

    @Override
    public GameProfile[] getVisiblePlayers() {
        return visiblePlayers;
    }

    @Override
    public void setVisiblePlayers(GameProfile[] players) {
        this.visiblePlayers = players;
    }

    @Override
    public void sayLmao() {
        TabFaces.info("LMAO");
    }
}
