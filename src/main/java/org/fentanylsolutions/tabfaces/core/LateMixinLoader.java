package org.fentanylsolutions.tabfaces.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@SuppressWarnings("unused")
@LateMixin
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.tabfaces.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        final List<String> mixins = new ArrayList<>();
        if (Loader.isModLoaded("tabbychat") && FMLLaunchHandler.side()
            .isClient()) {
            mixins.add("tabbychat.MixinGuiNewChatTC");
            mixins.add("tabbychat.MixinTabbyChat");
            mixins.add("tabbychat.MixinTCSettingSlider");
        }
        if (Loader.isModLoaded("serverutilities")) {
            mixins.add("serverutilities.MixinServerUtilitiesCommonAccessor");
        }
        return mixins;
    }
}
