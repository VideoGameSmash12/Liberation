package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Place a cage around someone with certain blocks, or someone's player head.", usage = "/<command> <purge | <partialname> [head | block] [playername | blockname]")
public class Command_cage extends FreedomCommand
{
    @Override
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        String skullName = null;
        if (args[0].equalsIgnoreCase("purge"))
        {
            FUtil.adminAction(sender.getName(), "Uncaging all players", true);
            for (Player player : server.getOnlinePlayers())
            {
                final FPlayer fPlayer = plugin.pl.getPlayer(player);
                fPlayer.getCageData().setCaged(false);
            }
            return true;
        }

        Player player = getPlayer(args[0]);
        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        final FPlayer fPlayer = plugin.pl.getPlayer(player);
        if (fPlayer.getCageData().isCaged())
        {
            msg("That player is already caged.", ChatColor.RED);
            return true;
        }

        Material outerMaterial = Material.GLASS;
        Material innerMaterial = Material.AIR;
        if (args.length >= 2 && args[1] != null)
        {
            final String s = args[1];
            switch (s)
            {
                case "head":
                {
                    outerMaterial = Material.PLAYER_HEAD;
                    if (args.length >= 3)
                    {
                        skullName = args[2];
                    }
                    else
                    {
                        outerMaterial = Material.SKELETON_SKULL;
                    }
                    break;
                }
                case "block":
                {
                    if (args.length >= 3)
                    {
                        // Checks the validity of the Material and checks if it's a block.
                        // This is incredibly inefficient, as Spigot's isBlock() method in Material is an actual
                        // nightmare of switch-cases.
                        if (Material.matchMaterial(args[2]) != null && Material.matchMaterial(args[2]).isBlock())
                        {
                            outerMaterial = Material.matchMaterial(args[2]);
                            break;
                        }
                        else
                        {
                            msg("Invalid block!", ChatColor.RED);
                            return true;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
                default:
                {
                    return false;
                }
            }
        }

        if (outerMaterial == Material.PLAYER_HEAD)
        {
            FUtil.adminAction(sender.getName(), "Caging " + player.getName() + " in " + skullName, true);
        }
        else
        {
            FUtil.adminAction(sender.getName(), "Caging " + player.getName(), true);
        }

        Location location = player.getLocation().clone().add(0.0, 1.0, 0.0);

        if (skullName != null)
        {
            fPlayer.getCageData().cage(location, outerMaterial, innerMaterial, skullName);
        }
        else
        {
            fPlayer.getCageData().cage(location, outerMaterial, innerMaterial);
        }
        player.setGameMode(GameMode.SURVIVAL);

        plugin.pul.logPunishment(new Punishment(player.getName(), FUtil.getIp(player), sender.getName(), PunishmentType.CAGE, null));
        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!plugin.al.isAdmin(sender))
        {
            return null;
        }

        if (args.length == 1)
        {
            List<String> arguments = new ArrayList<>();
            arguments.add("purge");
            arguments.addAll(FUtil.getPlayerList());
            return arguments;
        }
        else if (args.length == 2)
        {
            if (!args[0].equals("purge"))
            {
                return Arrays.asList("head", "block");
            }
        }
        else if (args.length == 3)
        {
            if (args[1].equals("block"))
            {
                return FUtil.getAllMaterialNames();
            }
            else if (args[1].equals("head"))
            {
                return FUtil.getPlayerList();
            }
        }

        return Collections.emptyList();
    }
}