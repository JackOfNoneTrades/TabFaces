package org.fentanylsolutions.tabfaces.access;

import com.mojang.authlib.GameProfile;

public interface IMixinServerData {

    public GameProfile[] getProfiles();

    void setProfiles(GameProfile[] profiles);
}
