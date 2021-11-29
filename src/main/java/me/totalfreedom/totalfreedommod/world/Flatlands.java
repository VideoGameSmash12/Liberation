package me.totalfreedom.totalfreedommod.world;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Flatlands extends CustomWorld
{

    private static final String GENERATION_PARAMETERS = ConfigEntry.FLATLANDS_GENERATE_PARAMS.getString();

    public Flatlands()
    {
        super("flatlands");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected World generateWorld()
    {
        if (!ConfigEntry.FLATLANDS_GENERATE.getBoolean())
        {
            return null;
        }

        final WorldCreator worldCreator = new WorldCreator(getName());
        worldCreator.generateStructures(false);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generator(new CleanroomChunkGenerator(GENERATION_PARAMETERS));

        final World world = Bukkit.getServer().createWorld(worldCreator);

        assert world != null;
        world.setSpawnFlags(false, false);
        world.setSpawnLocation(0, 50, 0);

        final Block welcomeSignBlock = world.getBlockAt(0, 50, 0);
        welcomeSignBlock.setType(Material.OAK_SIGN);
        org.bukkit.block.Sign welcomeSign = (org.bukkit.block.Sign)welcomeSignBlock.getState();

        org.bukkit.material.Sign signData = (org.bukkit.material.Sign)welcomeSign.getData();
        signData.setFacingDirection(BlockFace.NORTH);

        welcomeSign.setLine(0, ChatColor.GREEN + "Flatlands");
        welcomeSign.setLine(1, ChatColor.DARK_GRAY + "---");
        welcomeSign.setLine(2, ChatColor.YELLOW + "Spawn Point");
        welcomeSign.setLine(3, ChatColor.DARK_GRAY + "---");
        welcomeSign.update();

        plugin.gr.commitGameRules();

        return world;
    }
}
