package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows the amount of coins you or another player has. Also allows you to give coins to other players.", usage = "/<command> [player] | pay <player> <amount>")
public class Command_coins extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.SHOP_ENABLED.getBoolean())
        {
            msg("The shop is currently disabled!", ChatColor.RED);
            return true;
        }

        final String prefix = FUtil.colorize(ConfigEntry.SHOP_PREFIX.getString() + " ");

        switch (args.length)
        {
            // Mode for seeing how many coins the sender has (doesn't work from console)
            case 0:
            {
                if (senderIsConsole)
                {
                    msg("When used from the console, you must define a target player.");
                }
                else
                {
                    PlayerData playerData = getData(playerSender);
                    msg(prefix + ChatColor.GREEN + "You have " + ChatColor.RED + playerData.getCoins() + ChatColor.GREEN
                            + " coins.");
                }
                return true;
            }

            // Mode for seeing how many coins a player has.
            case 1:
            {
                Player target = getPlayer(args[0]);

                if (target == null)
                {
                    msg(PLAYER_NOT_FOUND);
                }
                else
                {
                    PlayerData playerData = getData(target);
                    msg(prefix + ChatColor.GREEN + target.getName() + " has " + ChatColor.RED + playerData.getCoins() + ChatColor.GREEN + " coins.");
                }
                return true;
            }

            // Mode for paying another player coins
            case 3:
            {
                if (args[0].equalsIgnoreCase("pay"))
                {
                    checkPlayer();

                    final Player target = getPlayer(args[1]);
                    final PlayerData senderData = getData(playerSender);

                    int coinsToTransfer;

                    // Processes args[2]
                    try
                    {
                        // Prevents players from trying to be cheeky with negative numbers.
                        coinsToTransfer = Math.max(Math.abs(Integer.parseInt(args[2])), 1);
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("Invalid number: " + args[2], ChatColor.RED);
                        return true;
                    }

                    // Prevents players from performing transactions they can't afford to do.
                    if (senderData.getCoins() < coinsToTransfer)
                    {
                        msg("You don't have enough coins to perform this transaction.", ChatColor.RED);
                        return true;
                    }

                    if (target == null)
                    {
                        msg(PLAYER_NOT_FOUND);
                    }
                    else
                    {
                        PlayerData playerData = getData(target);
                        playerData.setCoins(playerData.getCoins() + coinsToTransfer);
                        senderData.setCoins(senderData.getCoins() - coinsToTransfer);

                        msg(target, sender.getName()
                                + ChatColor.GREEN + " has given you "
                                + ChatColor.GOLD + coinsToTransfer
                                + ChatColor.GREEN + " coin" + (coinsToTransfer > 1 ? "s" : "") + "!", ChatColor.GOLD);

                        msg("You have given "
                                + ChatColor.GOLD + coinsToTransfer
                                + ChatColor.GREEN + " coin" + (coinsToTransfer > 1 ? "s" : "")
                                + " to " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + ".", ChatColor.GREEN);
                    }

                    return true;
                }
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
        if (args.length == 1)
        {
            List<String> options = new ArrayList<>(FUtil.getPlayerList());

            options.add("pay");

            return options;
        }

        return FUtil.getPlayerList();
    }
}