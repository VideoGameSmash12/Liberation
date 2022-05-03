package me.totalfreedom.totalfreedommod.rank;

import java.util.Objects;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class RankManager extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public Displayable getDisplay(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return getRank(sender); // Consoles don't have display ranks
        }

        final Player player = (Player)sender;

        // If the player's an owner, display that
        if (ConfigEntry.SERVER_OWNERS.getList().contains(player.getName()))
        {
            return Title.OWNER;
        }

        // Developers always show up
        if (FUtil.isDeveloper(player))
        {
            return Title.DEVELOPER;
        }

        if (ConfigEntry.SERVER_EXECUTIVES.getList().contains(player.getName()) && plugin.al.isAdmin(player))
        {
            return Title.EXECUTIVE;
        }
        
        if (ConfigEntry.SERVER_ASSISTANT_EXECUTIVES.getList().contains(player.getName()) && plugin.al.isAdmin(player))
        {
            return Title.ASSTEXEC;
        }

        // Master builders show up if they are not an admin
        if (plugin.pl.getData(player).isMasterBuilder() && !plugin.al.isAdmin(player))
        {
            return Title.MASTER_BUILDER;
        }

        return getRank(player);
    }

    public Displayable getDisplay(Admin admin)
    {
        // If the player's an owner, display that
        if (ConfigEntry.SERVER_OWNERS.getList().contains(admin.getName()))
        {
            return Title.OWNER;
        }

        // Developers always show up
        if (FUtil.isDeveloper((Player)admin))
        {
            return Title.DEVELOPER;
        }

        if (ConfigEntry.SERVER_EXECUTIVES.getList().contains(admin.getName()))
        {
            return Title.EXECUTIVE;
        }

        return admin.getRank();
    }

    public Rank getRank(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getRank((Player)sender);
        }

        // CONSOLE?
        if (sender.getName().equals("CONSOLE"))
        {
            return ConfigEntry.ADMINLIST_CONSOLE_IS_ADMIN.getBoolean() ? Rank.SENIOR_CONSOLE : Rank.ADMIN_CONSOLE;
        }

        // Console admin, get by name
        Admin admin = plugin.al.getEntryByName(sender.getName());

        // Unknown console: RCON?
        if (admin == null)
        {
            return Rank.SENIOR_CONSOLE;
        }

        Rank rank = admin.getRank();

        // Get console
        if (rank.hasConsoleVariant())
        {
            rank = rank.getConsoleVariant();
        }
        return rank;
    }

    public Rank getRank(Player player)
    {
        final Admin entry = plugin.al.getAdmin(player);
        if (entry != null)
        {
            return entry.getRank();
        }

        return player.isOp() ? Rank.OP : Rank.NON_OP;
    }

    public String getTag(Player player, String defaultTag)
    {
        String tag = defaultTag;

        PlayerData playerData = plugin.pl.getData(player);
        String t = playerData.getTag();
        if (t != null && !t.isEmpty())
        {
            tag = t;
        }

        return tag;
    }

    public void updateDisplay(Player player)
    {
        if (!player.isOnline())
        {
            return;
        }
        FPlayer fPlayer = plugin.pl.getPlayer(player);
        PlayerData data = plugin.pl.getData(player);
        Displayable display = getDisplay(player);
        if (plugin.al.isAdmin(player) || data.isMasterBuilder() || FUtil.isDeveloper(player))
        {
            String displayName = display.getColor() + player.getName();
            player.setPlayerListName(displayName);
        }
        else
        {
            fPlayer.setTag(null);
            player.setPlayerListName(null);
        }
        fPlayer.setTag(getTag(player, display.getColoredTag()));
        updatePlayerTeam(player);
        plugin.pem.setPermissions(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        PlayerData target = plugin.pl.getData(player);

        boolean isAdmin = plugin.al.isAdmin(player);

        // Updates last login time
        if (isAdmin)
        {
            plugin.al.updateLastLogin(player);
        } else
        {
            // Ensure admins don't have admin functionality when removed (FS-222)
            FPlayer freedomPlayer = plugin.pl.getPlayer(player);

            freedomPlayer.removeAdminFunctionality();
        }

        // Broadcast login message
        if (isAdmin || FUtil.isDeveloper(player) || plugin.pl.getData(player).isMasterBuilder() || plugin.pl.getData(player).hasLoginMessage())
        {
            if (!plugin.al.isVanished(player.getName()))
            {
                FUtil.bcastMsg(craftLoginMessage(player, null));
            }
        }

        // Set display
        updateDisplay(player);

        if (target.getTag() != null)
        {
            plugin.pl.getData(player).setTag(FUtil.colorize(target.getTag()));
        }
    }

    public String craftLoginMessage(Player player, String message)
    {
        Displayable display = plugin.rm.getDisplay(player);
        PlayerData playerData = plugin.pl.getData(player);
        if (message == null)
        {
            if (playerData.hasLoginMessage())
            {
                message = playerData.getLoginMessage();
            }
            else
            {
                if (display.hasDefaultLoginMessage())
                {
                    message = "%name% is %art% %coloredrank%";
                }
            }
        }
        if (message != null)
        {
            return FUtil.colorize(ChatColor.AQUA + (message.contains("%name%") ? "" : player.getName() + " is ")
                    + FUtil.colorize(message).replace("%name%", player.getName())
                    .replace("%rank%", display.getName())
                    .replace("%coloredrank%", display.getColoredName())
                    .replace("%art%", display.getArticle()));
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public void updatePlayerTeam(Player player)
    {
        Displayable display = getDisplay(player);
        Scoreboard scoreboard = Objects.requireNonNull(server.getScoreboardManager()).getMainScoreboard();
        Team team = scoreboard.getPlayerTeam(player);
        if (!display.hasTeam())
        {
            if (team != null)
            {
                team.removePlayer(player);
            }
            return;
        }
        String name = StringUtils.substring(display.toString(), 0, 16);
        team = scoreboard.getTeam(name);
        if (team == null)
        {
            team = scoreboard.registerNewTeam(name);
            team.setColor(display.getTeamColor());
        }
        if (!team.hasPlayer(player))
        {
            team.addPlayer(player);
        }
    }
}
