package me.totalfreedom.totalfreedommod.command;

import com.earth2me.essentials.User;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Unbans the specified player.", usage = "/<command> <username> [-r]")
public class Command_unban extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0)
        {
            String username;
            String ip;

            // Gets the IP using Essentials data if available
            if (plugin.esb.isEnabled() && plugin.esb.getEssentialsUser(args[0]) != null)
            {
                User essUser = plugin.esb.getEssentialsUser(args[0]);
                //
                username = essUser.getName();
                ip = essUser.getLastLoginAddress();
            }
            // Secondary method - using Essentials if available
            else
            {
                final PlayerData entry = plugin.pl.getData(args[0]);
                if (entry == null)
                {
                    msg(PLAYER_NOT_FOUND);
                    return true;
                }
                username = entry.getName();
                ip = entry.getIps().get(0);
            }

            FUtil.adminAction(sender.getName(), "Unbanning " + username, true);
            plugin.bm.removeBan(plugin.bm.getByUsername(username));
            plugin.bm.removeBan(plugin.bm.getByIp(ip));
            msg(username + " has been unbanned along with the IP: " + ip);

            if (args.length >= 2)
            {
                if (args[1].equalsIgnoreCase("-r"))
                {
                    plugin.cpb.restore(username);
                    msg("Restored edits for: " + username);
                }
            }
            return true;
        }
        return false;
    }
}