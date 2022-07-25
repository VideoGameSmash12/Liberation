package me.totalfreedom.totalfreedommod.world;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import org.bukkit.World;

@Deprecated(forRemoval = true)
public class WorldManager extends FreedomService
{
    @Override
    public void onStart()
    {
        // Disable weather
        if (ConfigEntry.DISABLE_WEATHER.getBoolean())
        {
            for (World world : server.getWorlds())
            {
                world.setThundering(false);
                world.setStorm(false);
                world.setThunderDuration(0);
                world.setWeatherDuration(0);
            }
        }
    }
}