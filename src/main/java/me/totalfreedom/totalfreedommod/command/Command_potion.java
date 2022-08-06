package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(
        description = "Manipulate your potion effects. Duration is measured in server ticks (~20 ticks per second).",
        usage = "/<command> <list | clearall | clear [target name] | add <type> <duration> <amplifier> [target name]>",
        aliases = "effect")
public class Command_potion extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        switch (args.length)
        {
            case 1:
            {
                if (args[0].equalsIgnoreCase("list"))
                {
                    List<String> potionEffectTypeNames = new ArrayList<>();
                    for (PotionEffectType potion_effect_type : PotionEffectType.values())
                    {
                        if (potion_effect_type != null)
                        {
                            potionEffectTypeNames.add(potion_effect_type.getName());
                        }
                    }
                    msg("Potion effect types: " + StringUtils.join(potionEffectTypeNames, ", "), ChatColor.AQUA);
                }
                else if (args[0].equalsIgnoreCase("clearall"))
                {
                    if (!(plugin.al.isAdmin(sender) || senderIsConsole))
                    {
                        noPerms();
                        return true;
                    }

                    FUtil.adminAction(sender.getName(), "Cleared all potion effects from all players", true);
                    for (Player target : server.getOnlinePlayers())
                    {
                        for (PotionEffect potion_effect : target.getActivePotionEffects())
                        {
                            target.removePotionEffect(potion_effect.getType());
                        }
                    }
                }
            }

            case 2:
            {
                if (args[0].equalsIgnoreCase("clear"))
                {
                    Player target = playerSender;
                    if (args.length == 2)
                    {
                        if (!plugin.al.isAdmin(sender) && !args[1].equalsIgnoreCase(sender.getName()))
                        {
                            msg(ChatColor.RED + "Only admins can clear potion effects from other players.");
                            return true;
                        }
                        target = getPlayer(args[1], true);
                    }
                    else
                    {
                        if (senderIsConsole)
                        {
                            msg("You must specify a target player when using this command from the console.");
                            return true;
                        }
                    }

                    if (target == null)
                    {
                        msg(FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
                        return true;
                    }

                    for (PotionEffect potion_effect : target.getActivePotionEffects())
                    {
                        target.removePotionEffect(potion_effect.getType());
                    }

                    msg("Cleared all active potion effects " + (!target.equals(playerSender) ? "from player " + target.getName() + "." : "from yourself."), ChatColor.AQUA);
                }
                break;
            }

            case 4:
            case 5:
            {
                if (args[0].equalsIgnoreCase("add"))
                {
                    Player target = playerSender;

                    if (args.length == 5)
                    {
                        if (!plugin.al.isAdmin(sender) && !args[4].equalsIgnoreCase(sender.getName()))
                        {
                            msg("Only admins can apply potion effects to other players.", ChatColor.RED);
                            return true;
                        }

                        target = getPlayer(args[4]);

                        if (target == null || plugin.al.isVanished(target.getName()) && !plugin.al.isAdmin(sender))
                        {
                            msg(PLAYER_NOT_FOUND);
                            return true;
                        }
                    }
                    else
                    {
                        if (senderIsConsole)
                        {
                            msg("You must specify a target player when using this command from the console.");
                            return true;
                        }
                    }

                    PotionEffectType potion_effect_type = PotionEffectType.getByName(args[1]);
                    if (potion_effect_type == null)
                    {
                        msg("Invalid potion effect type.", ChatColor.AQUA);
                        return true;
                    }

                    int duration;
                    try
                    {
                        duration = Integer.parseInt(args[2]);
                        duration = Math.min(duration, 100000);
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("Invalid potion duration.", ChatColor.RED);
                        return true;
                    }

                    int amplifier;
                    try
                    {
                        amplifier = Integer.parseInt(args[3]);
                        amplifier = Math.min(amplifier, 100000);
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("Invalid potion amplifier.", ChatColor.RED);
                        return true;
                    }

                    PotionEffect new_effect = potion_effect_type.createEffect(duration, amplifier);
                    target.addPotionEffect(new_effect);
                    msg(
                            "Added potion effect: " + new_effect.getType().getName()
                                    + ", Duration: " + new_effect.getDuration()
                                    + ", Amplifier: " + new_effect.getAmplifier()
                                    + (!target.equals(playerSender) ? " to player " + target.getName() + "." : " to yourself."), ChatColor.AQUA);
                }
                break;
            }
            default:
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        switch (args.length)
        {
            case 1:
            {
                List<String> arguments = new ArrayList<>(Arrays.asList("list", "clear", "add"));
                if (plugin.al.isAdmin(sender))
                {
                    arguments.add("clearall");
                }
                return arguments;
            }

            case 2:
            {
                if (args[0].equals("clear"))
                {
                    if (plugin.al.isAdmin(sender))
                    {
                        return FUtil.getPlayerList();
                    }
                }
                else if (args[0].equals("add"))
                {
                    return getAllPotionTypes();
                }
                break;
            }

            case 3:
            {
                if (args[0].equals("add"))
                {
                    return Collections.singletonList("<duration>");
                }
                break;
            }

            case 4:
            {
                if (args[0].equals("add"))
                {
                    return Collections.singletonList("<amplifier>");
                }
                break;
            }

            case 5:
            {
                if (plugin.al.isAdmin(sender))
                {
                    if (args[0].equals("add"))
                    {
                        return FUtil.getPlayerList();
                    }
                }
                break;
            }

            default:
            {
                break;
            }
        }

        return Collections.emptyList();
    }

    public List<String> getAllPotionTypes()
    {
        List<String> types = new ArrayList<>();
        for (PotionEffectType potionEffectType : PotionEffectType.values())
        {
            if (potionEffectType != null)
            {
                types.add(potionEffectType.getName());
            }
        }
        return types;
    }
}