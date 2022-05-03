package me.totalfreedom.totalfreedommod.admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import me.totalfreedom.totalfreedommod.LogViewer.LogsRegistrationMode;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Admin
{
    private final List<String> ips = new ArrayList<>();
    private UUID uuid;
    private boolean active = true;
    private Rank rank = Rank.ADMIN;
    private Date lastLogin = new Date();
    private Boolean commandSpy = false;
    private Boolean potionSpy = false;
    private String acFormat = null;
    private String pteroID = null;

    public Admin(Player player)
    {
        uuid = player.getUniqueId();
        this.ips.add(FUtil.getIp(player));
    }

    public Admin(ResultSet resultSet)
    {
        try
        {
            this.uuid = UUID.fromString(resultSet.getString("uuid"));
            this.active = resultSet.getBoolean("active");
            this.rank = Rank.findRank(resultSet.getString("rank"));
            this.ips.clear();
            this.ips.addAll(FUtil.stringToList(resultSet.getString("ips")));
            this.lastLogin = new Date(resultSet.getLong("last_login"));
            this.commandSpy = resultSet.getBoolean("command_spy");
            this.potionSpy = resultSet.getBoolean("potion_spy");
            this.acFormat = resultSet.getString("ac_format");
            this.pteroID = resultSet.getString("ptero_id");
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to load admin: " + e.getMessage());
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder output = new StringBuilder();

        output.append("Admin: ").append(getName()).append("\n")
                .append("- IPs: ").append(StringUtils.join(ips, ", ")).append("\n")
                .append("- Last Login: ").append(FUtil.dateToString(lastLogin)).append("\n")
                .append("- Rank: ").append(rank.getName()).append("\n")
                .append("- Is Active: ").append(active).append("\n")
                .append("- Potion Spy: ").append(potionSpy).append("\n")
                .append("- Admin Chat Format: ").append(acFormat).append("\n")
                .append("- Pterodactyl ID: ").append(pteroID).append("\n");

        return output.toString();
    }

    public Map<String, Object> toSQLStorable()
    {
        Map<String, Object> map = new HashMap<String, Object>()
        {{
            put("uuid", uuid.toString());
            put("active", active);
            put("rank", rank.toString());
            put("ips", FUtil.listToString(ips));
            put("last_login", lastLogin.getTime());
            put("command_spy", commandSpy);
            put("potion_spy", potionSpy);
            put("ac_format", acFormat);
            put("ptero_id", pteroID);
        }};
        return map;
    }

    // Util IP methods
    public void addIp(String ip)
    {
        if (!ips.contains(ip))
        {
            ips.add(ip);
        }
    }

    public void addIps(List<String> ips)
    {
        for (String ip : ips)
        {
            addIp(ip);
        }
    }

    public void removeIp(String ip)
    {
        ips.remove(ip);
    }

    public void clearIPs()
    {
        ips.clear();
    }

    public boolean isValid()
    {
        return uuid != null
                && rank != null
                && !ips.isEmpty()
                && lastLogin != null;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public String getName()
    {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;

        final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();

        // Avoiding stupid NPE compiler warnings
        if (plugin == null)
        {
            Bukkit.getLogger().severe("The plugin is null!! This is a major issue and WILL break the plugin!");
            return;
        }

        if (!active)
        {
            if (getRank().isAtLeast(Rank.ADMIN))
            {
                if (plugin.btb != null)
                {
                    plugin.btb.killTelnetSessions(getName());
                }

                // Ensure admins don't have admin functionality when removed (FS-222)
                AdminList.vanished.remove(getName());

                if (plugin.esb != null)
                {
                    plugin.esb.setVanished(getName(), false);
                }

                setCommandSpy(false);
                setPotionSpy(false);

                Server server = Bukkit.getServer();
                Player player = server.getPlayer(getName());

                if (player != null)
                {
                    // Update chats
                    FPlayer freedomPlayer = plugin.pl.getPlayer(player);
                    freedomPlayer.removeAdminFunctionality();

                    // Disable vanish
                    for (Player player1 : server.getOnlinePlayers())
                    {
                        player1.showPlayer(plugin, player);
                    }
                }

            }

            plugin.lv.updateLogsRegistration(null, getName(), LogsRegistrationMode.DELETE);
        }
    }

    public Rank getRank()
    {
        return rank;
    }

    public void setRank(Rank rank)
    {
        this.rank = rank;
    }

    public List<String> getIps()
    {
        return ips;
    }

    public Date getLastLogin()
    {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin)
    {
        this.lastLogin = lastLogin;
    }

    public Boolean getCommandSpy()
    {
        return commandSpy;
    }

    public void setCommandSpy(Boolean commandSpy)
    {
        this.commandSpy = commandSpy;
    }

    public Boolean getPotionSpy()
    {
        return potionSpy;
    }

    public void setPotionSpy(Boolean potionSpy)
    {
        this.potionSpy = potionSpy;
    }

    public String getAcFormat()
    {
        return acFormat;
    }

    public void setAcFormat(String acFormat)
    {
        this.acFormat = acFormat;
    }

    public String getPteroID()
    {
        return pteroID;
    }

    public void setPteroID(String pteroID)
    {
        this.pteroID = pteroID;
    }
}