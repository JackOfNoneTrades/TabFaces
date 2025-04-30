package org.fentanylsolutions.tabfaces.mixins.late.serverutilities;

import java.util.Map;
import java.util.function.Function;

import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import serverutils.ServerUtilitiesCommon;
import serverutils.lib.data.ForgePlayer;

@SuppressWarnings("unused")
@Mixin(ServerUtilitiesCommon.class)
public interface MixinServerUtilitiesCommonAccessor {

    @Accessor(value = "CHAT_FORMATTING_SUBSTITUTES", remap = false)
    public static Map<String, Function<ForgePlayer, IChatComponent>> getFormattingSubstitutes() {
        throw new AssertionError();
    }
}
