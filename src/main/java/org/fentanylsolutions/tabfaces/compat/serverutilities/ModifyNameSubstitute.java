package org.fentanylsolutions.tabfaces.compat.serverutilities;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatStyle;

import org.fentanylsolutions.tabfaces.mixins.late.serverutilities.MixinServerUtilitiesCommonAccessor;

public class ModifyNameSubstitute {

    /* We abuse the value of the click event to retrieve the marked chat component later */
    public static void substitute() {
        ChatStyle mark = new ChatStyle();
        MixinServerUtilitiesCommonAccessor.getFormattingSubstitutes()
            .put(
                "name",
                player -> player.getDisplayName()
                    .setChatStyle(
                        mark.setChatClickEvent(
                            new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/msg " + player.getDisplayName()
                                    .getUnformattedText()))));
    }
}
