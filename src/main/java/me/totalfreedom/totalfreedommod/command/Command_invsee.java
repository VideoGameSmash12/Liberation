package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Look into another player's inventory, or optionally take items out.", usage = "/<command> <player> [offhand | armor]", aliases = "inv,insee")
public class Command_invsee extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        Player player = getPlayer(args[0], true);

        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return false;
        }

        if (playerSender == player)
        {
            msg("You cannot run this command on yourself.", ChatColor.RED);
            return true;
        }

        if (plugin.al.isAdmin(player) && !plugin.al.isAdmin(playerSender))
        {
            msg("You cannot see the inventory of admins.", ChatColor.RED);
            return true;
        }

        Inventory inv;
        if (args.length > 1)
        {
            if (args[1].equals("offhand"))
            {
                ItemStack offhand = player.getInventory().getItemInOffHand();
                Inventory inventory = server.createInventory(null, 9, player.getName() + "'s offhand");
                inventory.setItem(1, offhand);
                playerSender.openInventory(inventory);
                return true;
            }
            else if (args[1].equals("armor"))
            {
                Inventory inventory = server.createInventory(null, 9, player.getName() + "'s armor");
                inventory.setContents(player.getInventory().getArmorContents());
                playerSender.openInventory(inventory);
                return true;
            }
        }
        inv = player.getInventory();
        playerSender.closeInventory();
        if (!plugin.al.isAdmin(player))
        {
            FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
            fPlayer.setInvSee(true);
        }
        playerSender.openInventory(inv);
        return true;
    }
}