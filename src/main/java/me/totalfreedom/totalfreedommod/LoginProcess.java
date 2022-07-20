package me.totalfreedom.totalfreedommod;

import io.papermc.lib.PaperLib;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FSync;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LoginProcess extends FreedomService
{
    public static final int DEFAULT_PORT = 25565;
    private static boolean lockdownEnabled = false;
    public List<String> TELEPORT_ON_JOIN = new ArrayList<>();
    public List<String> CLEAR_ON_JOIN = new ArrayList<>();
    public List<String> CLOWNFISH_TOGGLE = new ArrayList<>();

    public static boolean isLockdownEnabled()
    {
        return lockdownEnabled;
    }

    public static void setLockdownEnabled(boolean lockdownEnabled)
    {
        LoginProcess.lockdownEnabled = lockdownEnabled;
    }

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    /*
     * Banning and Permban checks are their respective services
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
    {
        final Admin entry = plugin.al.getEntryByUuid(event.getUniqueId());
        final boolean isAdmin = entry != null && entry.isActive();

        // Check if the player is already online
        for (Player onlinePlayer : server.getOnlinePlayers())
        {
            if (!onlinePlayer.getUniqueId().equals(event.getUniqueId()))
            {
                continue;
            }

            if (isAdmin)
            {
                event.allow();
                FSync.playerKick(onlinePlayer, "An admin just logged in with the username you are using.");
                return;
            }

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Your username is already logged into this server.");
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        // Check force-IP match
        if (ConfigEntry.FORCE_IP_ENABLED.getBoolean())
        {
            final String hostname = event.getHostname().replace("\u0000FML\u0000", ""); // Forge fix - https://github.com/TotalFreedom/TotalFreedomMod/issues/493
            final String connectAddress = ConfigEntry.SERVER_ADDRESS.getString();
            final int connectPort = server.getPort();

            if (!hostname.equalsIgnoreCase(connectAddress + ":" + connectPort) && !hostname.equalsIgnoreCase(connectAddress + ".:" + connectPort))
            {
                final int forceIpPort = ConfigEntry.FORCE_IP_PORT.getInteger();
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        ConfigEntry.FORCE_IP_KICKMSG.getString()
                                .replace("%address%", ConfigEntry.SERVER_ADDRESS.getString() + (forceIpPort == DEFAULT_PORT ? "" : ":" + forceIpPort)));
                return;
            }
        }

        // Validation below this point
        final Admin entry = plugin.al.getEntryByUuid(uuid);
        if (entry != null && entry.isActive()) // Check if player is admin
        {
            // Force-allow log in
            event.allow();

            int count = server.getOnlinePlayers().size();
            if (count >= server.getMaxPlayers())
            {
                for (Player onlinePlayer : server.getOnlinePlayers())
                {
                    if (!plugin.al.isAdmin(onlinePlayer))
                    {
                        onlinePlayer.kickPlayer("You have been kicked to free up room for an admin.");
                        count--;
                    }

                    if (count < server.getMaxPlayers())
                    {
                        break;
                    }
                }
            }

            if (count >= server.getMaxPlayers())
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "The server is full and a player could not be kicked, sorry!");
                return;
            }

            return;
        }

        // Player is not an admin
        // Server full check
        if (server.getOnlinePlayers().size() >= server.getMaxPlayers())
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Sorry, but this server is full.");
            return;
        }

        // Admin-only mode
        if (ConfigEntry.ADMIN_ONLY_MODE.getBoolean())
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is temporarily open to admins only.");
            return;
        }

        // Lockdown mode
        if (lockdownEnabled)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is currently in lockdown mode.");
            return;
        }

        // Whitelist
        if (server.isWhitelistEnforced() && !player.isWhitelisted())
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You are not whitelisted on this server.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final FPlayer fPlayer = plugin.pl.getPlayer(player);
        final PlayerData playerData = plugin.pl.getData(player);

        // Sends a message to the player if they have never joined before (or simply lack player data).
        if (!event.getPlayer().hasPlayedBefore() && ConfigEntry.FIRST_JOIN_INFO_ENABLED.getBoolean())
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    for (String line : ConfigEntry.FIRST_JOIN_INFO.getStringList())
                    {
                        player.sendMessage(FUtil.colorize(line));
                    }
                }
            }.runTaskLater(plugin, 20);
        }

        player.sendTitle(FUtil.colorize(ConfigEntry.SERVER_LOGIN_TITLE.getString()), FUtil.colorize(ConfigEntry.SERVER_LOGIN_SUBTITLE.getString()), 20, 100, 60);
        player.setOp(true);

        if (TELEPORT_ON_JOIN.contains(player.getName()) || ConfigEntry.AUTO_TP.getBoolean())
        {
            int x = FUtil.randomInteger(-10000, 10000);
            int z = FUtil.randomInteger(-10000, 10000);
            int y = player.getWorld().getHighestBlockYAt(x, z);
            Location location = new Location(player.getLocation().getWorld(), x, y, z);
            PaperLib.teleportAsync(player, location);
            player.sendMessage(ChatColor.AQUA + "You have been teleported to a random location automatically.");
            return;
        }

        if (!playerData.getIps().contains(FUtil.getIp(player)))
        {
            playerData.addIp(FUtil.getIp(player));
            plugin.pl.save(playerData);
        }

        if (CLEAR_ON_JOIN.contains(player.getName()) || ConfigEntry.AUTO_CLEAR.getBoolean())
        {
            player.getInventory().clear();
            player.sendMessage(ChatColor.AQUA + "Your inventory has been cleared automatically.");
            return;
        }

        if (!ConfigEntry.SERVER_TABLIST_HEADER.getString().isEmpty())
        {
            player.setPlayerListHeader(FUtil.colorize(ConfigEntry.SERVER_TABLIST_HEADER.getString()).replace("\\n", "\n"));
        }

        if (!ConfigEntry.SERVER_TABLIST_FOOTER.getString().isEmpty())
        {
            player.setPlayerListFooter(FUtil.colorize(ConfigEntry.SERVER_TABLIST_FOOTER.getString()).replace("\\n", "\n"));
        }

        if (!plugin.al.isAdmin(player))
        {
            String tag = playerData.getTag();
            if (tag != null)
            {
                fPlayer.setTag(FUtil.colorize(tag));
            }

            int noteCount = playerData.getNotes().size();
            if (noteCount != 0)
            {
                String noteMessage = "This player has " + noteCount + " admin note" + (noteCount > 1 ? "s" : "") + ".";
                FLog.info(noteMessage);
                plugin.al.messageAllAdmins(ChatColor.GOLD + noteMessage);
                plugin.al.messageAllAdmins(ChatColor.GOLD + "Do " + ChatColor.YELLOW + "/notes " + player.getName() + " list" + ChatColor.GOLD + " to view them.");
            }
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (ConfigEntry.ADMIN_ONLY_MODE.getBoolean())
                {
                    player.sendMessage(ChatColor.RED + "Server is currently closed to non-admins.");
                }

                if (lockdownEnabled)
                {
                    FUtil.playerMsg(player, "Warning: Server is currenty in lockdown-mode, new players will not be able to join!", ChatColor.RED);
                }
            }
        }.runTaskLater(plugin, 20L);
    }
}