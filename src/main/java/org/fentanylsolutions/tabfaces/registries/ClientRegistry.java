package org.fentanylsolutions.tabfaces.registries;

import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;

public class ClientRegistry {

    private ArrayList<Data> playerEntities;

    public ClientRegistry() {
        this.playerEntities = new ArrayList<>();
    }

    public void insert(String displayName, ResourceLocation skinResourceLocation) {
        if (getDataByDisplayName(displayName) == null) {
            this.playerEntities.add(new Data(displayName, skinResourceLocation));
        }
    }

    Data getDataByDisplayName(String displayName) {
        for (Data data : this.playerEntities) {
            if (data.displayName.equals(displayName)) {
                return data;
            }
        }
        return null;
    }

    public void removeByDisplayName(String displayName) {
        Data data = getDataByDisplayName(displayName);
        if (data != null) {
            this.playerEntities.remove(data);
        }
    }

    public void setTabMenuResourceLocation(String displayName, ResourceLocation tabMenuResourceLocation) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            insert(displayName, tabMenuResourceLocation);
            return;
        }
        data.tabMenuResourceLocation = tabMenuResourceLocation;
    }

    public ResourceLocation getTabMenuResourceLocation(String displayName) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            return null;
        }
        return data.tabMenuResourceLocation;
    }

    public void clear() {
        this.playerEntities = new ArrayList<>();
    }

    private class Data {

        String displayName;
        ResourceLocation tabMenuResourceLocation;

        Data(String displayName, ResourceLocation tabResourceLocation) {
            this.displayName = displayName;
            this.tabMenuResourceLocation = tabResourceLocation;
        }
    }
}
