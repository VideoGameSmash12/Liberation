package me.totalfreedom.totalfreedommod;

import org.bukkit.Server;
import org.bukkit.event.Listener;

public abstract class FreedomService implements Listener
{
    protected final TotalFreedomMod plugin;
    protected final Server server;

    public FreedomService()
    {
        plugin = TotalFreedomMod.getPlugin();
        server = plugin.getServer();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.fsh.add(this);
    }

    public void onStart()
    {
    }

    public void onStop()
    {
    }
}
