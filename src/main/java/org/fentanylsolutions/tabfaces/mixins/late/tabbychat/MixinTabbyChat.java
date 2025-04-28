package org.fentanylsolutions.tabfaces.mixins.late.tabbychat;

import org.fentanylsolutions.tabfaces.access.IMixinTabbyChat;
import org.fentanylsolutions.tabfaces.compat.tabbychat.TabFacesSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;

@SuppressWarnings("unused")
@Mixin(value = TabbyChat.class, remap = false)
public class MixinTabbyChat implements IMixinTabbyChat {

    private static TabFacesSettings tabFacesSettings;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(GuiNewChatTC gncInstance, CallbackInfo ci) {
        tabFacesSettings = new TabFacesSettings((TabbyChat) (Object) this);
    }

    @Override
    public TabFacesSettings getTabFacesSettings() {
        return tabFacesSettings;
    }
}
