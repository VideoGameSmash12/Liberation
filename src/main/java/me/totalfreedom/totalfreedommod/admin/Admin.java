package me.totalfreedom.totalfreedommod.admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.LogViewer.LogsRegistrationMode;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Admin
{


    private final List<String> ips = new ArrayList<>();
    private String name;
    private boolean active = true;
    private Rank rank = Rank.ADMIN;
    private Date lastLogin = new Date();
    private Boolean commandSpy = false;
    private Boolean potionSpy = false;
    private String acFormat = null;
    private String pteroID = null;

    public Admin(Player player)
    {
        this.name = player.getName();
        this.ips.add(FUtil.getIp(player));
    }

    public Admin(ResultSet resultSet)
    {
        try
        {
            this.name = resultSet.getString("username");
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

        output.append("Admin: ").append(name).append("\n")
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
            put("username", name);
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
        return name != null
                && rank != null
                && !ips.isEmpty()
                && lastLogin != null;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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