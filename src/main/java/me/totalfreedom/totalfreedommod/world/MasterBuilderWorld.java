package me.totalfreedom.totalfreedommod.world;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Sign;
import org.bukkit.entity.Player;

public final class MasterBuilderWorld extends CustomWorld
{

    private static final String GENERATION_PARAMETERS = ConfigEntry.FLATLANDS_GENERATE_PARAMS.getString();
    //
    private WorldWeather weather = WorldWeather.OFF;
    private WorldTime time = WorldTime.INHERIT;

    public MasterBuilderWorld()
    {
        super("masterbuilderworld");
    }

    @Override
    public void sendToWorld(Player player)
    {
        super.sendToWorld(player);
    }

    @Override
    protected World generateWorld()
    {
        final WorldCreator worldCreator = new WorldCreator(getName());
        worldCreator.generateStructures(false);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generator(new CleanroomChunkGenerator(GENERATION_PARAMETERS));

        final World world = server.createWorld(worldCreator);

        assert world != null;
        world.setSpawnFlags(false, false);
        world.setSpawnLocation(0, 50, 0);

        final Block welcomeSignBlock = world.getBlockAt(0, 50, 0);
        welcomeSignBlock.setType(Material.OAK_SIGN);
        org.bukkit.block.Sign welcomeSign = (org.bukkit.block.Sign) welcomeSignBlock.getState();
        ((Sign) welcomeSign.getBlockData()).setRotation(BlockFace.NORTH);

        welcomeSign.line(0, Component.text("MB World", TextColor.color(0x55FF55)));
        welcomeSign.line(1, Component.text("---", TextColor.color(0x555555)));
        welcomeSign.line(2, Component.text("Spawn Point", TextColor.color(0xFFFF55)));
        welcomeSign.line(3, Component.text("---", TextColor.color(0x555555)));
        welcomeSign.update();

        plugin.gr.commitGameRules();
        return world;
    }

    public WorldWeather getWeatherMode()
    {
        return weather;
    }

    public void setWeatherMode(final WorldWeather weatherMode)
    {
        this.weather = weatherMode;

        try
        {
            weatherMode.setWorldToWeather(getWorld());
        }
        catch (Exception ignored)
        {
        }
    }

    public WorldTime getTimeOfDay()
    {
        return time;
    }

    public void setTimeOfDay(final WorldTime timeOfDay)
    {
        this.time = timeOfDay;

        try
        {
            timeOfDay.setWorldToTime(getWorld());
        }
        catch (Exception ignored)
        {
        }
    }
}
