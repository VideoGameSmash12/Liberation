package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Toggle whether or not a player has the ability to use clownfish", usage = "/<command> <player>", aliases = "togglecf")
public class Command_toggleclownfish extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        boolean enabled = plugin.lp.CLOWNFISH_TOGGLE.contains(args[0]);

        if (enabled)
        {
            plugin.lp.CLOWNFISH_TOGGLE.remove(args[0]);
        }
        else
        {
            plugin.lp.CLOWNFISH_TOGGLE.add(args[0]);
        }

        msg(args[0] + " will " + (enabled ? "now" : "no longer") + " have the ability to use clownfish.");

        return true;
    }
}
