package org.fentanylsolutions.tabfaces.registries;

import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.GameProfile;

public class PlayerProfileRegistry {

    private final Map<String, GameProfile> nameProfileMap = new HashMap<>();
    private final Object mutex = new Object();

    public GameProfile getProfile(String playerName) {
        synchronized (mutex) {
            return nameProfileMap.get(playerName);
        }
    }

    public void setProfile(String playerName, GameProfile profile) {
        synchronized (mutex) {
            nameProfileMap.put(playerName, profile);
        }
    }

    public boolean hasProfile(String playerName) {
        synchronized (mutex) {
            return nameProfileMap.containsKey(playerName);
        }
    }

    public void removeProfile(String playerName) {
        synchronized (mutex) {
            nameProfileMap.remove(playerName);
        }
    }

    public void clear() {
        synchronized (mutex) {
            nameProfileMap.clear();
        }
    }
}
