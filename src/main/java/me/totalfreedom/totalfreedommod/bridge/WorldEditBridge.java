package me.totalfreedom.totalfreedommod.bridge;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldEditBridge extends FreedomService
{

    //
    private WorldEditPlugin worldeditPlugin = null;

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public WorldEditPlugin getWorldEditPlugin()
    {
        if (worldeditPlugin == null)
        {
            try
            {
                Plugin we = server.getPluginManager().getPlugin("WorldEdit");
                if (we != null)
                {
                    if (we instanceof WorldEditPlugin)
                    {
                        worldeditPlugin = (WorldEditPlugin)we;
                    }
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }

        return worldeditPlugin;
    }

    public void setLimit(Player player, int limit)
    {
        try
        {
            final LocalSession session = getPlayerSession(player);
            if (session != null)
            {
                session.setBlockChangeLimit(limit);
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }

    }

    public int getDefaultLimit()
    {
        final WorldEditPlugin wep = getWorldEditPlugin();
        if (wep == null)
        {
            return 0;
        }

        return wep.getLocalConfiguration().defaultChangeLimit;

    }

    public int getMaxLimit()
    {
        final WorldEditPlugin wep = getWorldEditPlugin();
        if (wep == null)
        {
            return 0;
        }

        return wep.getLocalConfiguration().maxChangeLimit;

    }

    private LocalSession getPlayerSession(Player player)
    {
        final WorldEditPlugin wep = getWorldEditPlugin();
        if (wep == null)
        {
            return null;
        }

        try
        {
            return wep.getSession(player);
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
            return null;
        }
    }
}
