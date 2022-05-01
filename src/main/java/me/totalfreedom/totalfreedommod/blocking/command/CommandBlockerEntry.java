package me.totalfreedom.totalfreedommod.blocking.command;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBlockerEntry
{


    private final CommandBlockerRank rank;

    private final CommandBlockerAction action;

    private final String command;

    private final String subCommand;

    private final String message;

    public CommandBlockerEntry(CommandBlockerRank rank, CommandBlockerAction action, String command, String message)
    {
        this(rank, action, command, null, message);
    }

    public CommandBlockerEntry(CommandBlockerRank rank, CommandBlockerAction action, String command, String subCommand, String message)
    {
        this.rank = rank;
        this.action = action;
        this.command = command;
        this.subCommand = ((subCommand == null) ? null : subCommand.toLowerCase().trim());
        this.message = ((message == null || message.equals("_")) ? "That command is blocked." : message);
    }

    public void doActions(CommandSender sender)
    {
        if (action == CommandBlockerAction.BLOCK_AND_EJECT && sender instanceof Player)
        {
            TotalFreedomMod.getPlugin().ae.autoEject((Player)sender, "You used a prohibited command: " + command);
            FUtil.bcastMsg(sender.getName() + " was automatically kicked for using harmful commands.", ChatColor.RED);
            return;
        }
        if (action == CommandBlockerAction.BLOCK_UNKNOWN)
        {
            sender.sendMessage(Bukkit.spigot().getSpigotConfig().getString("messages.unknown-command"));
            return;
        }
        FUtil.playerMsg(sender, FUtil.colorize(message));
    }

    public CommandBlockerRank getRank()
    {
        return rank;
    }

    public CommandBlockerAction getAction()
    {
        return action;
    }

    public String getCommand()
    {
        return command;
    }

    public String getSubCommand()
    {
        return subCommand;
    }

    public String getMessage()
    {
        return message;
    }
}