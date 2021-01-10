package me.totalfreedom.totalfreedommod.caging;

import io.papermc.lib.PaperLib;
import java.util.Objects;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Cager extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if (plugin.al.isAdmin(player))
        {
            return;
        }

        FPlayer fPlayer = plugin.pl.getPlayer(event.getPlayer());
        CageData cage = fPlayer.getCageData();

        if (cage.isCaged())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        FPlayer player = plugin.pl.getPlayer(event.getPlayer());
        CageData cage = player.getCageData();

        if (!cage.isCaged())
        {
            return;
        }

        Location playerLoc = player.getPlayer().getLocation().add(0, 1, 0);
        Location cageLoc = cage.getLocation();

        final boolean outOfCage;
        if (!Objects.equals(playerLoc.getWorld(), cageLoc.getWorld()))
        {
            outOfCage = true;
        }
        else
        {
            outOfCage = playerLoc.distanceSquared(cageLoc) > (2.5D * 2.5D);
        }

        if (outOfCage)
        {
            PaperLib.teleportAsync(player.getPlayer(), cageLoc.subtract(0, 0.1, 0));
            FUtil.playerMsg(player.getPlayer(), "You may not leave your cage.", ChatColor.RED);
            cage.regenerate();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        FPlayer player = plugin.pl.getPlayer(event.getPlayer());
        CageData cage = player.getCageData();

        if (cage.isCaged())
        {
            cage.playerQuit();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event)
    {
        FPlayer player = plugin.pl.getPlayer(event.getPlayer());
        CageData cage = player.getCageData();

        if (cage.isCaged())
        {
            cage.playerQuit();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        FPlayer player = plugin.pl.getPlayer(event.getPlayer());
        CageData cage = player.getCageData();

        if (cage.isCaged())
        {
            cage.playerJoin();
        }
    }
}