package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@CommandParameters(description = "Under contruction test command for the new reporting system", usage = "/<command> <player> [reason]")
@CommandPermissions(level = Rank.ADMIN, source = SourceType.ONLY_IN_GAME, blockHostConsole = true, cooldown = 5)
public class Command_testreport extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
            return false;

        OfflinePlayer player = server.getOfflinePlayerIfCached(args[0]);

        if (player == null || !player.hasPlayedBefore())
        {
            sender.sendMessage(Component.text("Player not found!").color(TextColor.color(0xFF5555)));
            return true;
        }

        plugin.rs.fileReport(playerSender, player, args.length >= 2 ? StringUtils.join(args, ' ', 1, args.length) : null);
        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length <= 1)
        {
            return FUtil.getPlayerList();
        }

        return Collections.emptyList();
    }
}
