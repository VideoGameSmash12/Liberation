package me.videogamesm12.liberation.services;

import com.google.gson.Gson;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.videogamesm12.liberation.world.CustomWorld;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * <h1>WorldManager</h1>
 * <p>Implements an easy to use world system that can be customized without needing to recompile the plugin.</p>
 */
public class WorldManager extends FreedomService
{
    public static final String FALLBACK_CLEANROOM_PARAMETERS = "16,stone,32,dirt,1,grass_block";

    // TODO: REMOVE THIS GARBAGE IMMEDIATELY
    public static final List<String> BLOCKED_WORLD_COMMANDS = Arrays.asList(
            "green", "fixlava", "fixwater", "br", "brush", "tool", "mat", "range", "cs", "up", "fill", "setblock",
            "tree", "replacenear", "ebigtree");

    // TODO: I AM DEAD FUCKING SERIOUS MOTHERFUCKER
    private static final String CONFIG_FILENAME = "worlds.json";

    private static final Gson GSON = new Gson();

    private WorldConfig worldConfig = null;

    @Override
    public void onStart()
    {
        File file = new File(plugin.getDataFolder(), CONFIG_FILENAME);
        if (!file.exists())
        {
            try
            {
                FileUtils.copyInputStreamToFile(Objects.requireNonNull(plugin.getResource(CONFIG_FILENAME)), file);
            }
            catch (Exception ex)
            {
                FLog.severe("Failed to copy world configuration!");
            }
        }

        try
        {
            WorldConfig config = GSON.fromJson(new FileReader(file), WorldConfig.class);

            if (worldConfig == null)
                worldConfig = config;
            else
                worldConfig.combine(config);
        }
        catch (Exception ex)
        {
            FLog.severe("World configuration failed to load. Worlds will not be generated!");
            FLog.severe(ex);
        }

        if (worldConfig.settings.enabled)
            worldConfig.worlds.values().forEach(CustomWorld::load);
    }

    @Override
    public void onStop()
    {
        if (worldConfig != null && worldConfig.settings.enabled)
            worldConfig.worlds.values().forEach(customWorld -> customWorld.getWorld().save());
    }

    /**
     * Get a CustomWorld from a World if it was loaded in by the TotalFreedomMod.
     * @param world World
     * @return CustomWorld
     */
    public CustomWorld getWorld(@NotNull World world)
    {
        if (worldConfig != null)
            return worldConfig.worlds.getOrDefault(world.getName(), null);
        else
            return null;
    }

    public boolean doRestrict(Player player)
    {
        if (worldConfig == null)
            return false;

        for (CustomWorld world : worldConfig.worlds.values())
        {
            if (world.doRestrict(player))
                return true;
        }

        return false;
    }

    public static class WorldConfig
    {
        private Settings settings;
        private Map<String, CustomWorld> worlds = new HashMap<>();

        public void combine(WorldConfig config)
        {
            config.worlds.forEach((key, value) -> {
                if (!worlds.containsKey(key))
                    worlds.put(key, value);
            });
        }

        public static class Settings
        {
            private boolean enabled;
        }
    }
}
