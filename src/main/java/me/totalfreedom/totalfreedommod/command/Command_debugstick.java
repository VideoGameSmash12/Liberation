package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import me.totalfreedom.totalfreedommod.rank.Rank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Get a stick of happiness.", usage = "/<command>")
public class Command_debugstick extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        ItemStack itemStack = new ItemStack(Material.DEBUG_STICK);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text("Stick of Happiness", TextColor.color(0xFFAA00)).decorate(TextDecoration.BOLD));
        itemMeta.lore(Arrays.asList(
                Component.text("This is the most powerful stick in the game.", TextColor.color(0xFF5555)),
                Component.text("You can left click to select what you want to change.", TextColor.color(0x0000AA)),
                Component.text("And then you can right click to change it!", TextColor.color(0x00AA00)),
                Component.text("Isn't technology amazing?", TextColor.color(0xAA00AA))
        ));

        itemStack.setItemMeta(itemMeta);
        playerSender.getInventory().addItem(itemStack);
        return true;
    }
}
