package me.totalfreedom.totalfreedommod.command;

import java.util.Objects;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Manage operators", usage = "/<command> <count | purge>")
public class Command_ops extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("count"))
        {
            int totalOps = server.getOperators().size();
            int onlineOps = 0;

            for (Player player : server.getOnlinePlayers())
            {
                if (player.isOp())
                {
                    onlineOps++;
                }
            }

            msg("Online OPs: " + onlineOps);
            msg("Offline OPs: " + (totalOps - onlineOps));
            msg("Total OPs: " + totalOps);
            return true;
        }

        if (args[0].equalsIgnoreCase("purge"))
        {
            if (!plugin.al.isAdmin(sender))
            {
                noPerms();
                return true;
            }

            FUtil.adminAction(sender.getName(), "Purging all operators", true);

            for (OfflinePlayer player : server.getOperators())
            {
                player.setOp(false);
                if (player.isOnline())
                {
                    msg(Objects.requireNonNull(player.getPlayer()), FreedomCommand.YOU_ARE_NOT_OP);
                }
            }
            return true;
        }
        return false;
    }
}