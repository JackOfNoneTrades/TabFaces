package org.fentanylsolutions.tabfaces.compat.skinport;

import java.util.UUID;

import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.TabFaces;

import com.mojang.authlib.GameProfile;

import lain.mods.skinport.init.forge.ClientProxy;
import lain.mods.skins.api.SkinProviderAPI;
import lain.mods.skins.api.interfaces.ISkin;
import lain.mods.skins.impl.PlayerProfile;

public class SkinPortCompat {

    public static ResourceLocation getSkinPortCachedSkin(UUID uuid, String displayName) {
        ISkin skin = SkinProviderAPI.SKIN.getSkin(PlayerProfile.wrapGameProfile(new GameProfile(uuid, displayName)));
        if (skin != null && skin.isDataReady()) {
            return ClientProxy.getOrCreateTexture(skin.getData(), skin)
                .getLocation();
        } else {
            return TabFaces.varInstanceClient.defaultResourceLocation;
        }
    }
}
