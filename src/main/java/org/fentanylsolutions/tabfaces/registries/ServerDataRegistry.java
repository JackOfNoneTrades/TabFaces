package org.fentanylsolutions.tabfaces.registries;

import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.GameProfile;

public class ServerDataRegistry {

    private final Map<String, GameProfile[]> ipProfileMap = new HashMap<>();
    private final Object mutex = new Object();

    public GameProfile[] getProfiles(String ipAddress) {
        synchronized (mutex) {
            return ipProfileMap.get(ipAddress);
        }
    }

    public void setProfiles(String ipAddress, GameProfile[] profiles) {
        synchronized (mutex) {
            ipProfileMap.put(ipAddress, profiles);
        }
    }

    public boolean hasProfiles(String ipAddress) {
        synchronized (mutex) {
            return ipProfileMap.containsKey(ipAddress);
        }
    }

    public void removeProfiles(String ipAddress) {
        synchronized (mutex) {
            ipProfileMap.remove(ipAddress);
        }
    }

    public void clear() {
        synchronized (mutex) {
            ipProfileMap.clear();
        }
    }
}
