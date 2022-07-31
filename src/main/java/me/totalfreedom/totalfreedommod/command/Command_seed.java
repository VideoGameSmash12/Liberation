package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Get the seed of the world you are currently in.", usage = "/seed [world]")
public class Command_seed extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        World world;

        if (args.length > 0)
        {
            world = server.getWorld(args[0]);
            if (world == null)
            {
                msg(Component.text("That world could not be found", TextColor.color(0xFF5555)));
                return true;
            }
        }
        else
        {
            // If the sender is a Player, use that world. Otherwise, use the overworld as a fallback.
            if (!senderIsConsole)
            {
                world = playerSender.getWorld();
            }
            else
            {
                world = server.getWorlds().get(0);
            }
        }

        String seed = String.valueOf(world.getSeed());

        TextComponent seedComponent = Component.text("[")
                .append(Component.text(seed, TextColor.color(0x55FF55))
                        .hoverEvent(HoverEvent.showText(Component.translatable("chat.copy.click")))
                        .clickEvent(ClickEvent.copyToClipboard(seed)))
                .append(Component.text("]"));

        sender.sendMessage(Component.translatable("commands.seed.success", seedComponent));

        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            // Returns a list of worlds on the server and returns it
            List<String> worlds = new ArrayList<>();
            for (World world : server.getWorlds())
            {
                worlds.add(world.getName());
            }
            return worlds;
        }

        return null;
    }
}