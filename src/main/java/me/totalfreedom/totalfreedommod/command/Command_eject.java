package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Eject any entities that are riding you.", usage = "/<command>")
public class Command_eject extends FreedomCommand
{
    /* Player.getShoulderEntityLeft() and Player.getShoulderEntityRight() are deprecated, however unless
        Player.getPassengers() also includes shoulders (which isn't likely, given the official documentation doesn't
        state an alternative method to use instead), these methods will continue to be used here. */

    @SuppressWarnings("deprecation")
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        // Uses the size of the return value of Player.getPassengers() as the starting number of entities ejected
        int count = playerSender.getPassengers().size();

        // Removes any entities from the sender's shoulders
        if (playerSender.getShoulderEntityLeft() != null)
        {
            playerSender.setShoulderEntityLeft(null);
            count++;
        }
        if (playerSender.getShoulderEntityRight() != null)
        {
            playerSender.setShoulderEntityLeft(null);
            count++;
        }

        // Removes anything riding the sender
        playerSender.eject();

        if (count != 0)
        {
            msg(count + " entit" + (count == 1 ? "y was" : "ies were") + " ejected.", ChatColor.GREEN);
        }
        else
        {
            msg("Nothing was ejected.", ChatColor.GREEN);
        }

        return true;
    }
}
