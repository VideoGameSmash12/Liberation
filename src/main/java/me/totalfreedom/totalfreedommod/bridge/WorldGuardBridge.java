package me.totalfreedom.totalfreedommod.bridge;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.totalfreedom.totalfreedommod.FreedomService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldGuardBridge extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public boolean canEditCurrentWorld(Player player)
    {
        // If WorldGuard integration is enabled, do a check with it.
        if (isEnabled())
        {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();

            return query.testBuild(localPlayer.getLocation(), localPlayer);
        }

        // If the plugin isn't present, return true.
        return true;
    }

    public boolean isEnabled()
    {
        Plugin plugin = server.getPluginManager().getPlugin("WorldGuard");

        return plugin != null && plugin.isEnabled();
    }
}