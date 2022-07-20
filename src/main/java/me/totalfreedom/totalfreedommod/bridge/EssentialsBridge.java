package me.totalfreedom.totalfreedommod.bridge;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EssentialsBridge extends FreedomService
{

    private Essentials essentialsPlugin = null;

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public Essentials getEssentialsPlugin()
    {
        if (essentialsPlugin == null)
        {
            try
            {
                final Plugin essentials = server.getPluginManager().getPlugin("Essentials");
                assert essentials != null;
                if (essentials instanceof Essentials)
                {
                    essentialsPlugin = (Essentials)essentials;
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }
        return essentialsPlugin;
    }

    public User getEssentialsUser(String username)
    {
        try
        {
            Essentials essentials = getEssentialsPlugin();
            if (essentials != null)
            {
                return essentials.getUserMap().getUser(username);
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return null;
    }

    public void setNickname(String username, String nickname)
    {
        try
        {
            User user = getEssentialsUser(username);
            if (user != null)
            {
                user.setNickname(nickname);
                user.setDisplayNick();
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    public String getNickname(String username)
    {
        try
        {
            User user = getEssentialsUser(username);
            if (user != null)
            {
                return user.getNickname();
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return null;
    }

    public long getLastActivity(String username)
    {
        try
        {
            User user = getEssentialsUser(username);
            if (user != null)
            {
                return user.getLastOnlineActivity();
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return 0L;
    }

    public void setVanished(String username, boolean vanished)
    {
        try
        {
            User user = getEssentialsUser(username);
            if (user != null)
            {
                user.setVanished(vanished);
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event)
    {
        Player refreshPlayer = null;
        Inventory inventory = event.getView().getTopInventory();
        InventoryType inventoryType = inventory.getType();
        Player player = (Player)event.getWhoClicked();
        FPlayer fPlayer = plugin.pl.getPlayer(player);
        if (inventoryType == InventoryType.PLAYER && fPlayer.isInvSee())
        {
            final InventoryHolder inventoryHolder = inventory.getHolder();
            if (inventoryHolder instanceof HumanEntity)
            {
                Player invOwner = (Player)inventoryHolder;
                Rank recieverRank = plugin.rm.getRank(player);
                Rank playerRank = plugin.rm.getRank(invOwner);
                if (playerRank.ordinal() >= recieverRank.ordinal() || !invOwner.isOnline())
                {
                    event.setCancelled(true);
                    refreshPlayer = player;
                }
            }
        }
        if (refreshPlayer != null)
        {
            final Player p = refreshPlayer;
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    p.updateInventory();
                }
            }.runTaskLater(plugin, 20L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event)
    {
        Player refreshPlayer = null;
        Inventory inventory = event.getView().getTopInventory();
        InventoryType inventoryType = inventory.getType();
        Player player = (Player)event.getPlayer();
        FPlayer fPlayer = plugin.pl.getPlayer(player);
        if (inventoryType == InventoryType.PLAYER && fPlayer.isInvSee())
        {
            fPlayer.setInvSee(false);
            refreshPlayer = player;
        }
        if (refreshPlayer != null)
        {
            final Player p = refreshPlayer;
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    p.updateInventory();
                }
            }.runTaskLater(plugin, 20L);
        }
    }

    public boolean isEnabled()
    {
        final Essentials ess = getEssentialsPlugin();

        return ess != null && ess.isEnabled();
    }
}