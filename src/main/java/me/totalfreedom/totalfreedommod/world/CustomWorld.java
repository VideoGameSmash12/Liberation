package me.totalfreedom.totalfreedommod.world;

import io.papermc.lib.PaperLib;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class CustomWorld extends FreedomService
{
    private final String name;
    //
    private World world;

    public CustomWorld(String name)
    {
        this.name = name;
    }

    public final World getWorld()
    {
        if (world == null || !Bukkit.getWorlds().contains(world))
        {
            world = generateWorld();
        }

        if (world == null)
        {
            FLog.warning("Could not load world: " + name);
        }

        return world;
    }

    public void sendToWorld(Player player)
    {
        try
        {
            PaperLib.teleportAsync(player, getWorld().getSpawnLocation());
        }
        catch (Exception ex)
        {
            player.sendMessage(ex.getMessage());
        }
    }

    protected abstract World generateWorld();

    public String getName()
    {
        return name;
    }
}
