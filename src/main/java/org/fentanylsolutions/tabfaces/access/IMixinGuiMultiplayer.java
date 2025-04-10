package org.fentanylsolutions.tabfaces.access;

import com.mojang.authlib.GameProfile;

public interface IMixinGuiMultiplayer {
    void setVisiblePlayers(GameProfile[] players);
}
