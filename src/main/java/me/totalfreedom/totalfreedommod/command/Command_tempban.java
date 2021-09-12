package me.totalfreedom.totalfreedommod.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import com.earth2me.essentials.User;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Temporarily ban someone.", usage = "/<command> [-q] <username> [duration] [reason]", aliases = "tban,noob")
public class Command_tempban extends FreedomCommand
{

    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        boolean quiet = args[0].equalsIgnoreCase("-q");
        if (quiet)
        {
            args = org.apache.commons.lang3.ArrayUtils.subarray(args, 1, args.length);

            if (args.length < 1)
            {
                return false;
            }
        }

        final String username;
        final String ip;

        final Player player = getPlayer(args[0]);
        PlayerData entry;
        if (player == null)
        {
            // Gets the IP using Essentials data if available
            if (plugin.esb.isEnabled() && plugin.esb.getEssentialsUser(args[0]) != null)
            {
                User essUser = plugin.esb.getEssentialsUser(args[0]);
                //
                username = essUser.getName();
                ip = essUser.getLastLoginAddress();
            }
            // Last resort - Getting the first result from the username itself
            else
            {
                entry = plugin.pl.getData(args[0]);
                if (entry == null)
                {
                    msg(PLAYER_NOT_FOUND);
                    return true;
                }
                else
                {
                    username = entry.getName();
                    ip = entry.getIps().get(0);
                }
            }
        }
        else
        {
            username = player.getName();
            ip = FUtil.getIp(player);
        }

        final StringBuilder message = new StringBuilder("Temporarily banned " + username);

        // Default expiration date is 5 minutes
        Date expires = FUtil.parseDateOffset("5m");

        // Parses what comes after as a duration
        if (args.length > 1)
        {
            try
            {
                expires = FUtil.parseDateOffset(args[1]);
            }
            catch (NumberFormatException error)
            {
                msg("Invalid duration: " + args[1], ChatColor.RED);
                return true;
            }
        }

        message.append(" until ").append(date_format.format(expires));

        // If a reason appears to exist, set it.
        String reason = null;
        if (args.length > 2)
        {
            reason = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ") + " (" + sender.getName() + ")";
            message.append(", Reason: \"").append(reason).append("\"");
        }

        Ban ban;
        if (player != null)
        {
            ban = Ban.forPlayer(player, sender, expires, reason);
        }
        else
        {
            ban = Ban.forPlayerName(username, sender, expires, reason);
        }
        ban.addIp(ip);

        plugin.bm.addBan(ban);

        if (!quiet)
        {
            if (player != null)
            {
                // Strike with lightning
                final Location targetPos = player.getLocation();
                for (int x = -1; x <= 1; x++)
                {
                    for (int z = -1; z <= 1; z++)
                    {
                        final Location strike_pos = new Location(targetPos.getWorld(), targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                        Objects.requireNonNull(targetPos.getWorld()).strikeLightningEffect(strike_pos);
                    }
                }

                player.kickPlayer(ban.bakeKickMessage());
            }

            FUtil.adminAction(sender.getName(), message.toString(), true);
        }
        else
        {
            msg("Quietly temporarily banned " + username + ".");
        }

        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (FUtil.getIp(p).equals(ip))
            {
                p.kickPlayer(ChatColor.RED + "You've been kicked because someone on your IP has been banned.");
            }
        }

        plugin.pul.logPunishment(new Punishment(username, ip, sender.getName(), PunishmentType.TEMPBAN, reason));
        return true;
    }
}