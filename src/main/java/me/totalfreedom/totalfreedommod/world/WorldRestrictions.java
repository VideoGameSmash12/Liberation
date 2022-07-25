package me.totalfreedom.totalfreedommod.world;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WorldRestrictions extends FreedomService
{

    private final List<String> BLOCKED_WORLDEDIT_COMMANDS = Arrays.asList(
            "green", "fixlava", "fixwater", "br", "brush", "tool", "mat", "range", "cs", "up", "fill", "setblock", "tree", "replacenear", "bigtree");

    private final List<String> BLOCKED_ESSENTIALS_COMMANDS = Arrays.asList(
            "bigtree", "ebigtree", "largetree", "elargetree");

    public boolean doRestrict(Player player)
    {
        if (!plugin.pl.getData(player).isMasterBuilder() && plugin.pl.canManageMasterBuilders(player.getName()))
        {
            if (player.getWorld().equals(plugin.wm.masterBuilderWorld.getWorld()))
            {
                return true;
            }
        }

        return !plugin.al.isAdmin(player) && player.getWorld().equals(plugin.wm.adminworld.getWorld());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player)
        {
            Player player = (Player)event.getDamager();

            if (doRestrict(player))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        final Player player = event.getPlayer();
        String command = event.getMessage().split("\\s+")[0].substring(1).toLowerCase();

        if (doRestrict(player))
        {
            /* This is a very poor way of blocking WorldEdit commands, all the methods I know of
               for obtaining a list of a plugin's commands are returning null for world edit. */
            String allowed = player.getWorld().equals(plugin.wm.adminworld.getWorld()) ? "Admins" : "Master Builders";

            if (command.startsWith("/") || BLOCKED_WORLDEDIT_COMMANDS.contains(command))
            {
                player.sendMessage(ChatColor.RED + "Only " + allowed + " are allowed to use WorldEdit here.");
                event.setCancelled(true);
            }

            if (command.equalsIgnoreCase("coreprotect") || command.equalsIgnoreCase("core") || command.equalsIgnoreCase("co"))
            {
                player.sendMessage(ChatColor.RED + "Only " + allowed + " are allowed to use CoreProtect here.");
                event.setCancelled(true);
            }
        }

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