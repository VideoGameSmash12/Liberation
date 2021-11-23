package me.totalfreedom.totalfreedommod.bridge;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.Map;

import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.totalfreedom.totalfreedommod.FreedomService;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldGuardBridge extends FreedomService
{
    @Override
    public void onStart()
    {
        plugin.wr.protectWorld(plugin.wm.masterBuilderWorld.getWorld());
    }

    @Override
    public void onStop()
    {
    }

    public boolean canEditCurrentWorld(Player player)
    {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        return query.testBuild(localPlayer.getLocation(), localPlayer);
    }

    public RegionManager getRegionManager(World world)
    {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(world));
    }

    public int wipeRegions(World world)
    {
        int count = 0;
        RegionManager regionManager = getRegionManager(world);
        if (regionManager != null)
        {
            Map<String, ProtectedRegion> regions = regionManager.getRegions();
            for (ProtectedRegion region : regions.values())
            {
                regionManager.removeRegion(region.getId());
                count++;
            }
        }
        return count;
    }

    public boolean isEnabled()
    {
        Plugin plugin = server.getPluginManager().getPlugin("WorldGuard");

        return plugin != null && plugin.isEnabled();
    }
}