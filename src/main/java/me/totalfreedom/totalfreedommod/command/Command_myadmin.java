package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manage your admin entry.", usage = "/<command> [-o <admin name>] <clearips | clearip <ip> | setscformat <format> | clearscformat>")
public class Command_myadmin extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        Player init = null;
        Admin target = getAdmin(playerSender);
        Player targetPlayer = playerSender;

        // -o switch
        if (args[0].equals("-o"))
        {
            checkRank(Rank.SENIOR_ADMIN);
            init = playerSender;
            targetPlayer = getPlayer(args[1]);
            if (targetPlayer == null)
            {
                msg(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }

            target = getAdmin(targetPlayer);
            if (target == null)
            {
                msg("That player is not an admin", ChatColor.RED);
                return true;
            }

            // Shift 2
            args = Arrays.copyOfRange(args, 2, args.length);
            if (args.length < 1)
            {
                return false;
            }
        }

        final String targetIp = FUtil.getIp(targetPlayer);

        switch (args[0])
        {
            case "clearips":
            {
                if (args.length != 1)
                {
                    return false; // Double check: the player might mean "clearip"
                }

                if (init == null)
                {
                    FUtil.adminAction(sender.getName(), "Clearing my IPs", true);
                }
                else
                {
                    FUtil.adminAction(sender.getName(), "Clearing " + target.getName() + "'s IPs", true);
                }

                int counter = target.getIps().size() - 1;
                target.clearIPs();
                target.addIp(targetIp);

                plugin.al.save(target);
                plugin.al.updateTables();
                plugin.pl.syncIps(target);

                msg(counter + " IPs removed.");
                msg(targetPlayer, target.getIps().get(0) + " is now your only IP address");
                return true;
            }

            case "clearip":
            {
                if (args.length != 2)
                {
                    return false; // Double check: the player might mean "clearips"
                }

                if (!target.getIps().contains(args[1]))
                {
                    if (init == null)
                    {
                        msg("That IP is not registered to you.");
                    }
                    else
                    {
                        msg("That IP does not belong to that player.");
                    }
                    return true;
                }

                if (targetIp.equals(args[1]))
                {
                    if (init == null)
                    {
                        msg("You cannot remove your current IP.");
                    }
                    else
                    {
                        msg("You cannot remove that admins current IP.");
                    }
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Removing an IP" + (init == null ? "" : " from " + targetPlayer.getName() + "'s IPs"), true);

                target.removeIp(args[1]);
                plugin.al.save(target);
                plugin.al.updateTables();

                plugin.pl.syncIps(target);

                msg("Removed IP " + args[1]);
                msg("Current IPs: " + StringUtils.join(target.getIps(), ", "));
                return true;
            }

            case "setacformat":
            case "setscformat":
            {
                String format = StringUtils.join(args, " ", 1, args.length);
                target.setAcFormat(format);
                plugin.al.save(target);
                plugin.al.updateTables();
                msg("Set admin chat format to \"" + format + "\".", ChatColor.GRAY);
                String example = format.replace("%name%", "ExampleAdmin").replace("%rank%", Rank.ADMIN.getAbbr()).replace("%rankcolor%", Rank.ADMIN.getColor().toString()).replace("%msg%", "The quick brown fox jumps over the lazy dog.");
                msg(ChatColor.GRAY + "Example: " + FUtil.colorize(example));
                return true;
            }

            case "clearacformat":
            case "clearscformat":
            {
                target.setAcFormat(null);
                plugin.al.save(target);
                plugin.al.updateTables();
                msg("Cleared admin chat format.", ChatColor.GRAY);
                return true;
            }

            default:
            {
                return false;
            }
        }
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!plugin.al.isAdmin(sender))
        {
            return Collections.emptyList();
        }

        List<String> singleArguments = Arrays.asList("clearips", "setscformat", "setacformat");
        List<String> doubleArguments = Arrays.asList("clearip", "clearscformat", "clearacformat");
        if (args.length == 1)
        {
            List<String> options = new ArrayList<>();
            options.add("-o");
            options.addAll(singleArguments);
            options.addAll(doubleArguments);
            return options;
        }
        else if (args.length == 2)
        {
            if (args[0].equals("-o"))
            {
                return FUtil.getPlayerList();
            }
            else
            {
                if (doubleArguments.contains(args[0]))
                {
                    if (args[0].equals("clearip"))
                    {
                        List<String> ips = plugin.al.getAdmin(sender).getIps();
                        ips.remove(FUtil.getIp((Player)sender));
                        return ips;
                    }
                }
            }
        }
        else if (args.length == 3)
        {
            if (args[0].equals("-o"))
            {
                List<String> options = new ArrayList<>();
                options.addAll(singleArguments);
                options.addAll(doubleArguments);
                return options;
            }
        }
        else if (args.length == 4)
        {
            if (args[0].equals("-o") && args[2].equals("clearip"))
            {
                Admin admin = plugin.al.getEntryByName(args[1]);
                if (admin != null)
                {
                    return admin.getIps();
                }
            }
        }
        return FUtil.getPlayerList();
    }
}