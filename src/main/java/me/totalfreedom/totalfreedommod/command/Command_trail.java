package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.shop.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Trails rainbow wool behind you as you walk/fly.", usage = "/<command>")
public class Command_trail extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.pl.getData(playerSender).hasItem(ShopItem.RAINBOW_TRAIL))
        {
            msg("You didn't purchase the ability to have a " + ShopItem.RAINBOW_TRAIL.getName() + "! Purchase it from the shop.", ChatColor.RED);
            return true;
        }

        if (plugin.tr.contains(playerSender))
        {
            plugin.tr.remove(playerSender);
            msg("Trail disabled.");
        }
        else
        {
            plugin.tr.add(playerSender);
            msg("Trail enabled. Run this command again to disable it.");
        }

        return true;
    }
}