package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Block all commands for everyone on the server, or a specific player.", usage = "/<command> <-a | purge | <player>>", aliases = "blockcommands,blockcommand,bc,bcmd")
public class Command_blockcmd extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("purge"))
        {
            FUtil.adminAction(sender.getName(), "Unblocking commands for all players", true);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                FPlayer playerdata = plugin.pl.getPlayer(player);
                if (playerdata.allCommandsBlocked())
                {
                    counter += 1;
                    playerdata.setCommandsBlocked(false);
                }
            }
            msg("Unblocked commands for " + counter + " players.");
            return true;
        }
        else if (args[0].equalsIgnoreCase("-a"))
        {
            FUtil.adminAction(sender.getName(), "Blocking commands for all non-admins", true);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (isAdmin(player))
                {
                    continue;
                }

                counter += 1;
                plugin.pl.getPlayer(player).setCommandsBlocked(true);
                msg(player, "Your commands have been blocked by an admin.", ChatColor.RED);
            }

            msg("Blocked commands for " + counter + " players.");
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        if (isAdmin(player))
        {
            msg(player.getName() + " is an admin, and cannot have their commands blocked.");
            return true;
        }

        FPlayer playerdata = plugin.pl.getPlayer(player);
        if (!playerdata.allCommandsBlocked())
        {
            FUtil.adminAction(sender.getName(), "Blocking all commands for " + player.getName(), true);
            playerdata.setCommandsBlocked(true);
            msg("Blocked commands for " + player.getName() + ".");

            plugin.pul.logPunishment(new Punishment(player.getName(), FUtil.getIp(player), sender.getName(), PunishmentType.BLOCKCMD, null));
        }
        else
        {
            msg("That players commands are already blocked.", ChatColor.RED);
        }
        return true;
    }
}