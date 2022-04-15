package me.totalfreedom.totalfreedommod.admin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminList extends FreedomService
{
    public static final List<String> vanished = new ArrayList<>();
    private final Set<Admin> allAdmins = Sets.newHashSet(); // Includes disabled admins
    // Only active admins below
    private final Set<Admin> activeAdmins = Sets.newHashSet();
    private final Map<UUID, Admin> uuidTable = Maps.newHashMap();
    private final Map<String, Admin> nameTable = Maps.newHashMap();
    private final Map<String, Admin> ipTable = Maps.newHashMap();

    public static List<String> getVanished()
    {
        return vanished;
    }

    @Override
    public void onStart()
    {
        load();
        deactivateOldEntries(false);
    }

    @Override
    public void onStop()
    {
    }

    public void load()
    {
        allAdmins.clear();
        try
        {
            ResultSet adminSet = plugin.sql.getAdminList();
            {
                while (adminSet.next())
                {
                    Admin admin = new Admin(adminSet);
                    allAdmins.add(admin);
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to load admin list: " + e.getMessage());
        }

        updateTables();
        FLog.info("Loaded " + allAdmins.size() + " admins (" + uuidTable.size() + " active, " + ipTable.size() + " IPs)");
    }

    public void messageAllAdmins(String message)
    {
        for (Player player : server.getOnlinePlayers())
        {
            if (isAdmin(player))
            {
                player.sendMessage(message);
            }
        }
    }

    public void potionSpyMessage(String message)
    {
        for (Player player : server.getOnlinePlayers())
        {
            Admin admin = getAdmin(player.getPlayer());
            if (isAdmin(player) && admin.getPotionSpy())
            {
                player.sendMessage(message);
            }
        }
    }

    public synchronized boolean isAdminSync(CommandSender sender)
    {
        return isAdmin(sender);
    }

    public List<String> getActiveAdminNames()
    {
        List<String> names = new ArrayList();
        for (Admin admin : activeAdmins)
        {
            names.add(admin.getName());
        }
        return names;
    }

    public boolean isAdmin(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        Admin admin = getAdmin((Player)sender);

        return admin != null && admin.isActive();
    }

    public boolean isAdmin(Player player)
    {
        if (player == null)
        {
            return true;
        }

        Admin admin = getAdmin(player);

        return admin != null && admin.isActive();
    }

    public boolean isSeniorAdmin(CommandSender sender)
    {
        Admin admin = getAdmin(sender);
        if (admin == null)
        {
            return false;
        }

        return admin.getRank().ordinal() >= Rank.SENIOR_ADMIN.ordinal();
    }

    public Admin getAdmin(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getAdmin((Player)sender);
        }

        return getEntryByName(sender.getName());
    }

    public Admin getAdmin(Player player)
    {
        final String ip = FUtil.getIp(player);
        final Admin entry = getEntryByUuid(player.getUniqueId());

        if (entry != null && !entry.getIps().contains(ip))
        {
            entry.addIp(ip);
        }

        return entry;
    }

    public Admin getEntryByUuid(UUID uuid)
    {
        return uuidTable.get(uuid);
    }

    public Admin getEntryByName(String name)
    {
        return nameTable.get(name.toLowerCase());
    }

    public Admin getEntryByIp(String ip)
    {
        return ipTable.get(ip);
    }

    public void updateLastLogin(Player player)
    {
        final Admin admin = getAdmin(player);
        if (admin == null)
        {
            return;
        }

        admin.setLastLogin(new Date());
        save(admin);
    }

    public boolean addAdmin(Admin admin)
    {
        if (!admin.isValid())
        {
            FLog.warning("Could not add admin: " + admin.getName() + ". Admin is missing details!");
            return false;
        }

        // Store admin, update views
        allAdmins.add(admin);
        updateTables();

        // Save admin
        plugin.sql.addAdmin(admin);

        return true;
    }

    public boolean removeAdmin(Admin admin)
    {
        if (admin.getRank().isAtLeast(Rank.ADMIN))
        {
            if (plugin.btb != null)
            {
                plugin.btb.killTelnetSessions(admin.getName());
            }
        }

        // Remove admin, update views
        if (!allAdmins.remove(admin))
        {
            return false;
        }
        updateTables();

        // Unsave admin
        plugin.sql.removeAdmin(admin);

        return true;
    }

    public void updateTables()
    {
        activeAdmins.clear();
        uuidTable.clear();
        nameTable.clear();
        ipTable.clear();

        for (Admin admin : allAdmins)
        {
            if (!admin.isActive())
            {
                continue;
            }

            activeAdmins.add(admin);
            uuidTable.put(admin.getUuid(), admin);
            nameTable.put(admin.getName().toLowerCase(), admin);

            for (String ip : admin.getIps())
            {
                ipTable.put(ip, admin);
            }
        }
    }

    public Set<String> getAdminNames()
    {
        return nameTable.keySet();
    }

    public Set<String> getAdminIps()
    {
        return ipTable.keySet();
    }

    public void save(Admin admin)
    {
        try
        {
            ResultSet currentSave = plugin.sql.getAdminByUuid(admin.getUuid());
            for (Map.Entry<String, Object> entry : admin.toSQLStorable().entrySet())
            {
                Object storedValue = plugin.sql.getValue(currentSave, entry.getKey(), entry.getValue());                if (storedValue != null && !storedValue.equals(entry.getValue()) || storedValue == null && entry.getValue() != null || entry.getValue() == null)
                {
                    plugin.sql.setAdminValue(admin, entry.getKey(), entry.getValue());
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to save admin: " + e.getMessage());
        }
    }

    public void deactivateOldEntries(boolean verbose)
    {
        for (Admin admin : allAdmins)
        {
            if (!admin.isActive() || admin.getRank().isAtLeast(Rank.SENIOR_ADMIN))
            {
                continue;
            }

            final Date lastLogin = admin.getLastLogin();
            final long lastLoginHours = TimeUnit.HOURS.convert(new Date().getTime() - lastLogin.getTime(), TimeUnit.MILLISECONDS);

            if (lastLoginHours < ConfigEntry.ADMINLIST_CLEAN_THESHOLD_HOURS.getInteger())
            {
                continue;
            }

            if (verbose)
            {
                FUtil.adminAction("TotalFreedomMod", "Deactivating admin " + admin.getName() + ", inactive for " + lastLoginHours + " hours", true);
            }

            admin.setActive(false);
            save(admin);
        }

        updateTables();
    }

    public boolean isVanished(String player)
    {
        return vanished.contains(player);
    }

    public Set<Admin> getAllAdmins()
    {
        return allAdmins;
    }

    public Set<Admin> getActiveAdmins()
    {
        return activeAdmins;
    }

    public Map<String, Admin> getNameTable()
    {
        return nameTable;
    }

    public Map<String, Admin> getIpTable()
    {
        return ipTable;
    }
}