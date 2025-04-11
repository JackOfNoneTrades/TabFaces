package org.fentanylsolutions.tabfaces.mixins.early.minecraft;

import net.minecraft.client.multiplayer.ServerData;

import org.fentanylsolutions.tabfaces.access.IMixinServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.mojang.authlib.GameProfile;

@Mixin(ServerData.class) // Replace TargetClassHere with the actual Minecraft class
public class MixinServerData implements IMixinServerData {

    @Unique
    GameProfile[] profiles;

    @Override
    public GameProfile[] getProfiles() {
        return this.profiles;
    }

    @Override
    public void setProfiles(GameProfile[] profiles) {
        this.profiles = profiles;
    }
}
