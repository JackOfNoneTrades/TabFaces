package org.fentanylsolutions.tabfaces.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import org.fentanylsolutions.tabfaces.Config;
import org.fentanylsolutions.tabfaces.TabFaces;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandDebugCleanup implements ICommand {

    private static final String TEAM_NAME = "tf_dbg";
    private final List aliases = new ArrayList();

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "tabfaces_dbg_cleanup";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/tabfaces_dbg_cleanup - Removes the debug scoreboard team";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayerMP && !FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .getConfigurationManager()
            .func_152596_g(((EntityPlayerMP) sender).getGameProfile())) {
            sender.addChatMessage(new ChatComponentText("\u00a7cYou do not have permission to use this command."));
            return;
        }

        if (!Config.debugInjectServerStatusProfiles) {
            sender.addChatMessage(new ChatComponentText("\u00a7cDebug injection is not enabled."));
            return;
        }

        Scoreboard scoreboard = MinecraftServer.getServer()
            .worldServerForDimension(0)
            .getScoreboard();
        ScorePlayerTeam team = scoreboard.getTeam(TEAM_NAME);

        if (team == null) {
            sender.addChatMessage(new ChatComponentText("\u00a7eNo debug team found."));
            return;
        }

        int memberCount = team.getMembershipCollection()
            .size();
        scoreboard.removeTeam(team);
        sender.addChatMessage(new ChatComponentText("\u00a7aRemoved debug team with " + memberCount + " members."));
        TabFaces.info(sender.getCommandSenderName() + " cleaned up debug scoreboard team");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}
