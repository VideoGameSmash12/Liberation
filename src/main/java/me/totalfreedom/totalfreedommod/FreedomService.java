package me.totalfreedom.totalfreedommod;

import java.util.logging.Logger;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.Server;
import org.bukkit.event.Listener;

public abstract class FreedomService implements Listener
{
    protected final TotalFreedomMod plugin;
    protected final Server server;
    protected final Logger logger;

    public FreedomService()
    {
        plugin = TotalFreedomMod.getPlugin();
        server = plugin.getServer();
        logger = FLog.getPluginLogger();
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
