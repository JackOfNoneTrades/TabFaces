package org.fentanylsolutions.tabfaces.compat.wawelauth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ResourceLocation;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.wawelauth.api.WawelFaceRendererClient;
import org.fentanylsolutions.wawelauth.api.WawelTextureResolver;
import org.fentanylsolutions.wawelauth.wawelclient.IServerDataExt;
import org.fentanylsolutions.wawelauth.wawelclient.ServerCapabilities;
import org.fentanylsolutions.wawelauth.wawelclient.WawelClient;
import org.fentanylsolutions.wawelauth.wawelclient.data.ClientAccount;
import org.fentanylsolutions.wawelauth.wawelclient.data.ClientProvider;

public class WawelAuthCompat {

    /**
     * Resolve a skin for a player while in-game.
     * Uses WawelClient.resolvePlayerProvider to find the right provider.
     */
    public static ResourceLocation getSkin(UUID uuid, String displayName) {
        WawelClient client = WawelClient.instance();
        if (client == null) {
            return null;
        }

        try {
            ClientProvider provider = client.resolvePlayerProvider(uuid);
            if (provider == null) {
                return null;
            }
            return client.getTextureResolver()
                .getSkin(uuid, displayName, provider, false);
        } catch (Exception e) {
            TabFaces.debug("WawelAuth skin lookup failed for " + displayName + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Resolve a skin for a player shown in the server selection tooltip.
     *
     * <ul>
     * <li>WawelAuth server: try the local auth provider + all advertised fallback providers.</li>
     * <li>Unknown server with a bound account: use that account's provider.</li>
     * <li>Unknown server with no bound account: don't fetch (returns null).</li>
     * </ul>
     */
    public static ResourceLocation getSkinForServerTooltip(UUID uuid, String displayName, ServerData serverData) {
        WawelClient client = WawelClient.instance();
        if (client == null || uuid == null) {
            return null;
        }

        try {
            ServerCapabilities capabilities = getCapabilities(serverData);

            if (capabilities != null && capabilities.isWawelAuthAdvertised()) {
                // WawelAuth server: try all advertised providers
                List<ClientProvider> providers = buildProvidersFromCapabilities(client, capabilities);
                if (providers.isEmpty()) {
                    return null;
                }
                return client.getTextureResolver()
                    .getSkinFromAnyProvider(uuid, displayName, providers);
            }

            // Unknown server: check if user bound an account to this server
            ClientProvider boundProvider = getBoundAccountProvider(client, serverData);
            if (boundProvider != null) {
                return client.getTextureResolver()
                    .getSkin(uuid, displayName, boundProvider, false);
            }

            // Unknown server, no bound account: don't fetch
            return null;
        } catch (Exception e) {
            TabFaces.debug("WawelAuth server tooltip skin lookup failed for " + displayName + ": " + e.getMessage());
            return null;
        }
    }

    public static boolean isPlaceholder(ResourceLocation resourceLocation) {
        return resourceLocation == null || WawelTextureResolver.getDefaultSkin()
            .equals(resourceLocation);
    }

    public static void drawFace(ResourceLocation resourceLocation, float xPos, float yPos, float alpha) {
        WawelFaceRendererClient.drawFace(resourceLocation, xPos, yPos, alpha);
    }

    // ------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------

    private static ServerCapabilities getCapabilities(ServerData serverData) {
        if (serverData instanceof IServerDataExt) {
            return ((IServerDataExt) serverData).getWawelCapabilities();
        }
        return null;
    }

    /**
     * Build the list of providers a WawelAuth server advertises:
     * the local auth provider (if supported) plus all accepted fallback providers.
     */
    private static List<ClientProvider> buildProvidersFromCapabilities(WawelClient client,
        ServerCapabilities capabilities) {
        List<ClientProvider> providers = new ArrayList<>();

        // 1. Local auth provider (the server's own auth)
        if (capabilities.isLocalAuthSupported()) {
            try {
                ClientProvider localProvider = client.getLocalAuthProviderResolver()
                    .findExisting(capabilities);
                if (localProvider != null) {
                    providers.add(localProvider);
                }
            } catch (Exception e) {
                TabFaces.debug("Failed to resolve local auth provider: " + e.getMessage());
            }
        }

        // 2. Accepted fallback providers from descriptors
        for (ServerCapabilities.AcceptedProviderDescriptor descriptor : capabilities.getAcceptedProviders()) {
            if (descriptor == null) {
                continue;
            }
            ClientProvider resolved = resolveDescriptorToProvider(client, descriptor);
            if (resolved != null) {
                providers.add(resolved);
            }
        }

        return providers;
    }

    /**
     * Try to match an accepted provider descriptor to a known ClientProvider.
     * Falls back to building an ephemeral provider from the descriptor's URLs.
     */
    private static ClientProvider resolveDescriptorToProvider(WawelClient client,
        ServerCapabilities.AcceptedProviderDescriptor descriptor) {

        // Try matching by name first
        String name = descriptor.getName();
        if (name != null && !name.trim()
            .isEmpty()) {
            ClientProvider byName = client.getProviderRegistry()
                .getProvider(name);
            if (byName != null) {
                return byName;
            }
        }

        // Build an ephemeral provider from the descriptor URLs
        ClientProvider ephemeral = new ClientProvider();
        ephemeral.setName(name != null ? name : "ephemeral-tooltip");
        ephemeral.setApiRoot(descriptor.getApiRoot());
        ephemeral.setAuthServerUrl(descriptor.getAuthServerUrl());
        ephemeral.setSessionServerUrl(descriptor.getSessionServerUrl());
        ephemeral.setServicesUrl(descriptor.getServicesUrl());
        String publicKey = descriptor.getSignaturePublicKeyBase64();
        if (publicKey != null && !publicKey.trim()
            .isEmpty()) {
            ephemeral.setPublicKeyBase64(publicKey.trim());
        }
        return ephemeral;
    }

    /**
     * Get the provider for the account bound to this server entry, or null.
     */
    private static ClientProvider getBoundAccountProvider(WawelClient client, ServerData serverData) {
        if (!(serverData instanceof IServerDataExt)) {
            return null;
        }

        IServerDataExt ext = (IServerDataExt) serverData;
        long accountId = ext.getWawelAccountId();
        if (accountId < 0) {
            return null;
        }

        ClientAccount account = client.getAccountManager()
            .getAccount(accountId);
        if (account == null) {
            return null;
        }

        String providerName = account.getProviderName();
        if (providerName == null || providerName.trim()
            .isEmpty()) {
            return null;
        }

        return client.resolveProviderByName(providerName);
    }
}
