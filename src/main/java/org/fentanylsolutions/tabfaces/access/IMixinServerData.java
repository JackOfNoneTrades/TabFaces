package org.fentanylsolutions.tabfaces.access;

import com.mojang.authlib.GameProfile;

/* https://mixin-wiki.readthedocs.io/tricks/#access-new-mixin-functions-from-other-packages */
public interface IMixinServerData {

    // GameProfile[] visiblePlayers = null;
    GameProfile[] getVisiblePlayers();

    void setVisiblePlayers(GameProfile[] players);

    void sayLmao();
}
