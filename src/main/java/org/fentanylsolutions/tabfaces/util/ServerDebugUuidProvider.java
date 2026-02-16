package org.fentanylsolutions.tabfaces.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.fentanylsolutions.tabfaces.TabFaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

public final class ServerDebugUuidProvider {

    private static final File DEBUG_UUIDS_FILE = new File("config/debug_uuids.json");
    private static final long STAT_POLL_INTERVAL_MS = 2000L;
    private static final GameProfile[] EMPTY = new GameProfile[0];

    private static final Object LOCK = new Object();
    private static volatile long lastPollMs = 0L;
    private static volatile long lastKnownModified = Long.MIN_VALUE;
    private static volatile GameProfile[] cachedProfiles = EMPTY;
    private static volatile boolean missingFileLogged = false;

    private ServerDebugUuidProvider() {}

    public static void ensureDebugFileExists() {
        synchronized (LOCK) {
            if (DEBUG_UUIDS_FILE.isFile()) {
                return;
            }

            try {
                File parent = DEBUG_UUIDS_FILE.getParentFile();
                if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
                    TabFaces.warn("Failed to create directory for " + DEBUG_UUIDS_FILE.getPath());
                    return;
                }

                try (FileOutputStream out = new FileOutputStream(DEBUG_UUIDS_FILE)) {
                    out.write("{\n}\n".getBytes(StandardCharsets.UTF_8));
                }
                TabFaces.info("Created " + DEBUG_UUIDS_FILE.getPath());
                missingFileLogged = false;
                lastKnownModified = Long.MIN_VALUE;
                cachedProfiles = EMPTY;
            } catch (Exception e) {
                TabFaces.warn("Failed to create " + DEBUG_UUIDS_FILE.getPath() + ": " + e.getMessage());
            }
        }
    }

    public static GameProfile[] getDebugProfiles() {
        long now = System.currentTimeMillis();
        if (now - lastPollMs < STAT_POLL_INTERVAL_MS) {
            return cachedProfiles;
        }

        synchronized (LOCK) {
            if (now - lastPollMs < STAT_POLL_INTERVAL_MS) {
                return cachedProfiles;
            }
            lastPollMs = now;

            if (!DEBUG_UUIDS_FILE.isFile()) {
                if (!missingFileLogged) {
                    TabFaces.debug("Server debug UUID file not found: " + DEBUG_UUIDS_FILE.getPath());
                    missingFileLogged = true;
                }
                cachedProfiles = EMPTY;
                lastKnownModified = Long.MIN_VALUE;
                return cachedProfiles;
            }

            missingFileLogged = false;
            long modified = DEBUG_UUIDS_FILE.lastModified();
            if (modified == lastKnownModified) {
                return cachedProfiles;
            }

            cachedProfiles = loadProfilesFromDisk();
            lastKnownModified = modified;
            TabFaces
                .info("Loaded " + cachedProfiles.length + " debug status profiles from " + DEBUG_UUIDS_FILE.getPath());
            return cachedProfiles;
        }
    }

    private static GameProfile[] loadProfilesFromDisk() {
        List<GameProfile> profiles = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(
            new FileInputStream(DEBUG_UUIDS_FILE),
            StandardCharsets.UTF_8)) {
            JsonElement rootElement = new JsonParser().parse(reader);
            if (!rootElement.isJsonObject()) {
                TabFaces.warn("debug_uuids.json must be a JSON object: {\"Name\":\"uuid\"}");
                return EMPTY;
            }

            JsonObject rootObject = rootElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : rootObject.entrySet()) {
                String name = entry.getKey() != null ? entry.getKey()
                    .trim() : "";
                if (name.isEmpty()) {
                    continue;
                }

                JsonElement value = entry.getValue();
                if (value == null || !value.isJsonPrimitive()
                    || !value.getAsJsonPrimitive()
                        .isString()) {
                    TabFaces.warn("Skipping debug UUID for " + name + ": value is not a string");
                    continue;
                }

                String uuidString = value.getAsString()
                    .trim();
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    profiles.add(new GameProfile(uuid, name));
                } catch (IllegalArgumentException e) {
                    TabFaces.warn("Skipping debug UUID for " + name + ": invalid UUID '" + uuidString + "'");
                }
            }
        } catch (Exception e) {
            TabFaces.warn("Failed reading debug UUIDs from " + DEBUG_UUIDS_FILE.getPath() + ": " + e.getMessage());
            return EMPTY;
        }

        return profiles.toArray(new GameProfile[0]);
    }
}
