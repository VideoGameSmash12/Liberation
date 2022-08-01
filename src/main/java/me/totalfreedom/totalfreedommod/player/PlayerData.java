package me.totalfreedom.totalfreedommod.player;

import com.google.common.collect.Lists;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import me.totalfreedom.totalfreedommod.shop.ShopItem;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerData
{
    private UUID uuid;
    private final List<String> ips = Lists.newArrayList();
    private final List<String> notes = Lists.newArrayList();
    private String tag = null;
    private Boolean masterBuilder = false;


    private String rideMode = "ask";


    private int coins;
    private List<String> items = Lists.newArrayList();
    private int totalVotes;
    private String loginMessage;
    private Boolean inspect = false;

    public PlayerData(ResultSet resultSet)
    {
        try
        {
            uuid = UUID.fromString(resultSet.getString("uuid"));
            ips.clear();
            ips.addAll(FUtil.stringToList(resultSet.getString("ips")));
            notes.clear();
            notes.addAll(FUtil.stringToList(resultSet.getString("notes")));
            tag = resultSet.getString("tag");
            masterBuilder = resultSet.getBoolean("master_builder");
            rideMode = resultSet.getString("ride_mode");
            coins = resultSet.getInt("coins");
            items.clear();
            items.addAll(FUtil.stringToList(resultSet.getString("items")));
            totalVotes = resultSet.getInt("total_votes");
            loginMessage = resultSet.getString("login_message");
            inspect = resultSet.getBoolean("inspect");
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to load player: " + e.getMessage());
        }
    }

    public PlayerData(Player player)
    {
        this.uuid = player.getUniqueId();
    }

    @Override
    public String toString()
    {
        return "Player: " + getName() + "\n" +
                "- IPs: " + StringUtils.join(ips, ", ") + "\n" +
                "- Master Builder: " + masterBuilder + "\n" +
                "- Coins: " + coins + "\n" +
                "- Total Votes: " + totalVotes + "\n" +
                "- Tag: " + FUtil.colorize(tag) + ChatColor.GRAY + "\n" +
                "- Ride Mode: " + rideMode + "\n" +
                "- Login Message: " + loginMessage;
    }

    public List<String> getIps()
    {
        return Collections.unmodifiableList(ips);
    }

    public boolean hasLoginMessage()
    {
        return loginMessage != null && !loginMessage.isEmpty();
    }

    public boolean addIp(String ip)
    {
        if (ips.contains(ip))
        {
            return false;
        }
        ips.add(ip);
        return true;
    }

    public void removeIp(String ip)
    {
        ips.remove(ip);
    }

    public void clearIps()
    {
        ips.clear();
    }

    public void addIps(List<String> ips)
    {
        this.ips.addAll(ips);
    }

    public List<String> getNotes()
    {
        return Collections.unmodifiableList(notes);
    }

    public void clearNotes()
    {
        notes.clear();
    }

    public void addNote(String note)
    {
        notes.add(note);
    }

    public boolean removeNote(int id) throws IndexOutOfBoundsException
    {
        try
        {
            notes.remove(id);
        }
        catch (IndexOutOfBoundsException e)
        {
            return false;
        }
        return true;
    }

    public void giveItem(ShopItem item)
    {
        items.add(item.getDataName());
    }

    public List<String> getItems()
    {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<String> items)
    {
        this.items = items;
    }

    public boolean hasItem(ShopItem item)
    {
        return items.contains(item.getDataName());
    }

    public void removeItem(ShopItem item)
    {
        items.remove(item.getDataName());
    }

    public boolean isMasterBuilder()
    {
        return masterBuilder;
    }

    public boolean hasInspection()
    {
        return inspect;
    }

    public Map<String, Object> toSQLStorable()
    {
        return new HashMap<String, Object>()
        {{
            put("uuid", uuid.toString());
            put("ips", FUtil.listToString(ips));
            put("notes", FUtil.listToString(notes));
            put("tag", tag);
            put("master_builder", masterBuilder);
            put("ride_mode", rideMode);
            put("coins", coins);
            put("items", FUtil.listToString(items));
            put("total_votes", totalVotes);
            put("login_message", loginMessage);
            put("inspect", inspect);
        }};
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public String getName()
    {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public Boolean getMasterBuilder()
    {
        return masterBuilder;
    }

    public void setMasterBuilder(Boolean masterBuilder)
    {
        this.masterBuilder = masterBuilder;
    }

    public String getRideMode()
    {
        return rideMode;
    }

    public void setRideMode(String rideMode)
    {
        this.rideMode = rideMode;
    }

    public int getCoins()
    {
        return coins;
    }

    public void setCoins(int coins)
    {
        this.coins = coins;
    }

    public int getTotalVotes()
    {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes)
    {
        this.totalVotes = totalVotes;
    }

    public String getLoginMessage()
    {
        return loginMessage;
    }

    public void setLoginMessage(String loginMessage)
    {
        this.loginMessage = loginMessage;
    }

    public Boolean getInspect()
    {
        return inspect;
    }

    public void setInspect(Boolean inspect)
    {
        this.inspect = inspect;
    }
}