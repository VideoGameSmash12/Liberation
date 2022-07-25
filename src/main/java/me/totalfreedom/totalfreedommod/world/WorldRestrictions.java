package me.totalfreedom.totalfreedommod.world;

import java.util.Arrays;
import java.util.List;
import me.totalfreedom.totalfreedommod.FreedomService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@Deprecated(forRemoval = true)
public class WorldRestrictions extends FreedomService
{
    private final List<String> BLOCKED_ESSENTIALS_COMMANDS = Arrays.asList(
            "bigtree", "ebigtree", "largetree", "elargetree");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        final Player player = event.getPlayer();
        String command = event.getMessage().split("\\s+")[0].substring(1).toLowerCase();

        if (player.getWorld().equals(Bukkit.getWorld("plotworld")))
        {
            if (BLOCKED_ESSENTIALS_COMMANDS.contains(command))
            {
                player.sendMessage(ChatColor.RED + "Sorry, this command is restricted in the plotworld");
                event.setCancelled(true);
            }
        }
    }
}