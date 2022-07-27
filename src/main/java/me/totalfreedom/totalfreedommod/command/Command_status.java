package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows Minecraft server info, such as authentication status.", usage = "/<command>")
public class Command_status extends FreedomCommand
{
    @Override
    public boolean run(final CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        msg("For information about TotalFreedomMod, try /tfm", ChatColor.GREEN); // Temporary

        msg("Server is currently running with 'online-mode=" + (server.getOnlineMode() ? "true" : "false") + "'.", ChatColor.YELLOW);
        msg("Loaded worlds:", ChatColor.BLUE);
        int i = 0;
        for (World world : server.getWorlds())
        {
            msg(String.format("World %d: %s - %d players.", i++, world.getName(), world.getPlayers().size()), ChatColor.BLUE);
        }

        return true;
    }
}
