package me.totalfreedom.totalfreedommod.bridge;

import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CoreProtectBridge extends FreedomService
{
    public static Map<Player, FUtil.PaginationList<String>> HISTORY_MAP = new HashMap<>();
    private final HashMap<String, Long> cooldown = new HashMap<>();
    private CoreProtectAPI coreProtectAPI = null;

    public static Long getSecondsLeft(long prevTime, int timeAdd)
    {
        return prevTime / 1000L + timeAdd - System.currentTimeMillis() / 1000L;
    }

    // Unix timestamp converter taken from Functions class in CoreProtect, not my code
    public static String getTimeAgo(int logTime, int currentTime)
    {
        StringBuilder message = new StringBuilder();
        double timeSince = (double)currentTime - ((double)logTime + 0.0D);
        timeSince /= 60.0D;
        if (timeSince < 60.0D)
        {
            message.append((new DecimalFormat("0.00")).format(timeSince)).append("/m ago");
        }

        if (message.length() == 0)
        {
            timeSince /= 60.0D;
            if (timeSince < 24.0D)
            {
                message.append((new DecimalFormat("0.00")).format(timeSince)).append("/h ago");
            }
        }

        if (message.length() == 0)
        {
            timeSince /= 24.0D;
            message.append((new DecimalFormat("0.00")).format(timeSince)).append("/d ago");
        }

        return message.toString();
    }

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public CoreProtect getCoreProtect()
    {
        CoreProtect coreProtect = null;
        try
        {
            final Plugin coreProtectPlugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");
            assert coreProtectPlugin != null;
            if (coreProtectPlugin instanceof CoreProtect)
            {
                coreProtect = (CoreProtect)coreProtectPlugin;
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return coreProtect;
    }

    public CoreProtectAPI getCoreProtectAPI()
    {
        if (coreProtectAPI == null)
        {
            try
            {
                final CoreProtect coreProtect = getCoreProtect();

                coreProtectAPI = coreProtect.getAPI();

                // Check if the plugin or api is not enabled, if so, return null
                if (!coreProtect.isEnabled() || !coreProtectAPI.isEnabled())
                {
                    return null;
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }

        return coreProtectAPI;
    }

    public boolean isEnabled()
    {
        final CoreProtect coreProtect = getCoreProtect();

        return coreProtect != null && coreProtect.isEnabled();
    }

    // Rollback the specified player's edits that were in the last 24 hours.
    public void rollback(final String name)
    {
        final CoreProtectAPI coreProtect = getCoreProtectAPI();

        if (!isEnabled())
        {
            return;
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                coreProtect.performRollback(86400, Collections.singletonList(name), null, null, null, null, 0, null);
            }
        }.runTaskAsynchronously(plugin);
    }

    // Reverts a rollback for the specified player's edits that were in the last 24 hours.
    public void restore(final String name)
    {
        final CoreProtectAPI coreProtect = getCoreProtectAPI();

        if (!isEnabled())
        {
            return;
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                coreProtect.performRestore(86400, Collections.singletonList(name), null, null, null, null, 0, null);
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        PlayerData data = plugin.pl.getData(player);
        Block block = event.getClickedBlock();
        final CoreProtectAPI coreProtect = getCoreProtectAPI();

        // TODO: Rewrite this
        if (data.hasInspection())
        {
            int cooldownTime = 3;

            // Cooldown check
            if ((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                    && cooldown.containsKey(player.getName()))
            {
                long secondsLeft = getSecondsLeft(cooldown.get(player.getName()), cooldownTime);
                if (secondsLeft > 0L)
                {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + String.valueOf(secondsLeft) + " seconds left before next query.");
                    return;
                }
            }

            // Actual lookup time
            if (event.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                if (block != null)
                {
                    event.setCancelled(true);
                    List<String[]> lookup = coreProtect.blockLookup(block, -1);

                    if (!plugin.al.isAdmin(player))
                    {
                        cooldown.put(player.getName(), System.currentTimeMillis());
                    }

                    if (lookup != null)
                    {
                        if (lookup.isEmpty())
                        {
                            player.sendMessage(net.md_5.bungee.api.ChatColor.of("#30ade4") + "Block Inspector " + ChatColor.WHITE + "- " + "No block data found for this location");
                            return;
                        }

                        HISTORY_MAP.remove(event.getPlayer());
                        HISTORY_MAP.put(event.getPlayer(), new FUtil.PaginationList<>(10));
                        FUtil.PaginationList<String> paged = HISTORY_MAP.get(event.getPlayer());

                        player.sendMessage("---- " + net.md_5.bungee.api.ChatColor.of("#30ade4") + "Block Inspector" + ChatColor.WHITE + " ---- " +
                                ChatColor.GRAY + "(x" + block.getX() + "/" + "y" + block.getY() + "/" + "z" + block.getZ() + ")");

                        for (String[] value : lookup)
                        {
                            CoreProtectAPI.ParseResult result = coreProtect.parseResult(value);
                            BlockData bl = result.getBlockData();

                            String s;
                            String st = "";

                            if (result.getActionString().equals("Placement"))
                            {
                                s = " placed ";
                            }
                            else if (result.getActionString().equals("Removal"))
                            {
                                s = " broke ";
                            }
                            else
                            {
                                s = " interacted with ";
                            }

                            if (result.isRolledBack())
                            {
                                st += "§m";
                            }

                            int time = (int)(System.currentTimeMillis() / 1000L);

                            paged.add(ChatColor.GRAY + getTimeAgo(result.getTime(), time) + ChatColor.WHITE + " - " + net.md_5.bungee.api.ChatColor.of("#30ade4") +
                                    st + result.getPlayer() + ChatColor.WHITE + st + s + net.md_5.bungee.api.ChatColor.of("#30ade4") + st + bl.getMaterial().toString().toLowerCase());
                        }

                        List<String> page = paged.getPage(1);
                        for (String entries : page)
                        {
                            player.sendMessage(entries);
                        }

                        player.sendMessage("Page 1/" + paged.getPageCount() + " | To index through the pages, type " + net.md_5.bungee.api.ChatColor.of("#30ade4") + "/ins history <page>");
                    }
                }
            }
            else if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                if (block != null)
                {
                    BlockState blockState = block.getRelative(event.getBlockFace()).getState();
                    Block placedBlock = blockState.getBlock();
                    event.setCancelled(true);
                    List<String[]> lookup = coreProtect.blockLookup(placedBlock, -1);

                    if (lookup.isEmpty())
                    {
                        lookup = coreProtect.blockLookup(block, -1);
                    }

                    if (!plugin.al.isAdmin(player))
                    {
                        cooldown.put(player.getName(), System.currentTimeMillis());
                    }

                    if (lookup != null)
                    {
                        if (lookup.isEmpty())
                        {
                            player.sendMessage(net.md_5.bungee.api.ChatColor.of("#30ade4") + "Block Inspector " + ChatColor.WHITE + "- " + "No block data found for this location");
                            return;
                        }

                        HISTORY_MAP.remove(event.getPlayer());
                        HISTORY_MAP.put(event.getPlayer(), new FUtil.PaginationList<>(10));
                        FUtil.PaginationList<String> paged = HISTORY_MAP.get(event.getPlayer());

                        player.sendMessage("---- " + net.md_5.bungee.api.ChatColor.of("#30ade4") + "Block Inspector" + ChatColor.WHITE + " ---- " +
                                ChatColor.GRAY + "(x" + block.getX() + "/" + "y" + block.getY() + "/" + "z" + block.getZ() + ")");

                        for (String[] value : lookup)
                        {
                            CoreProtectAPI.ParseResult result = coreProtect.parseResult(value);
                            BlockData bl = result.getBlockData();

                            String s;
                            String st = "";

                            if (result.getActionString().equals("Placement"))
                            {
                                s = " placed ";
                            }
                            else if (result.getActionString().equals("Removal"))
                            {
                                s = " broke ";
                            }
                            else
                            {
                                s = " interacted with ";
                            }

                            if (result.isRolledBack())
                            {
                                st += "§m";
                            }

                            int time = (int)(System.currentTimeMillis() / 1000L);

                            paged.add(ChatColor.GRAY + getTimeAgo(result.getTime(), time) + ChatColor.WHITE + " - " + net.md_5.bungee.api.ChatColor.of("#30ade4") +
                                    st + result.getPlayer() + ChatColor.WHITE + st + s + net.md_5.bungee.api.ChatColor.of("#30ade4") + st + bl.getMaterial().toString().toLowerCase());
                        }

                        List<String> page = paged.getPage(1);
                        for (String entries : page)
                        {
                            player.sendMessage(entries);
                        }

                        player.sendMessage("Page 1/" + paged.getPageCount() + " | To index through the pages, type " + net.md_5.bungee.api.ChatColor.of("#30ade4") + "/ins history <page>");
                    }
                }
            }
        }
    }
}