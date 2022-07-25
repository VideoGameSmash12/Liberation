package me.videogamesm12.liberation.world;

import io.papermc.lib.PaperLib;
import lombok.Data;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.command.SourceType;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.world.CleanroomChunkGenerator;
import me.videogamesm12.liberation.services.CustomWorldManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

@Getter
public class CustomWorld implements Listener
{
    private String name;
    private Command command = null;
    private Generation generation;
    private Flags flags;
    private Permissions permissions;
    private Settings settings;
    //--
    private World world = null;

    public final void load()
    {
        if (world == null || Bukkit.getWorld(name) == null || !Bukkit.getWorlds().contains(world))
        {
            world = generate();

            // Sets up protection-oriented event listeners
            Bukkit.getPluginManager().registerEvents(this, TotalFreedomMod.getPlugin());
        }

        // Registers the command for the world
        if (command != null && command.enabled && Bukkit.getCommandMap().getCommand(name.replace(" ", "_").toLowerCase()) == null)
            TotalFreedomMod.getPlugin().cl.add(new WorldCommand(this));
    }

    public final World generate()
    {
        WorldCreator worldCreator = new WorldCreator(name);

        // Environment, type, and whether to generate structures
        worldCreator.environment(generation.environment);
        worldCreator.type(generation.type);
        worldCreator.generateStructures(generation.generateStructures);

        // Should we use the Cleanroom Generator?
        if (generation.useCleanroomGeneration)
            worldCreator.generator(new CleanroomChunkGenerator(generation.cleanroomParameters));

        world = worldCreator.createWorld();

        if (world == null)
        {
            throw new IllegalStateException("World did not generate properly. What gives?");
        }

        // Flags
        world.setSpawnFlags(flags.allowAnimals, flags.allowMonsters);

        return world;
    }

    public boolean doRestrict(Player player)
    {
        PlayerData data = TotalFreedomMod.getPlugin().pl.getData(player);

        // Failsafe
        if (data == null)
        {
            return true;
        }

        return !TotalFreedomMod.getPlugin().rm.getRank(player).isAtLeast(permissions.minModifyRank)
                || (permissions.mbRequired && !data.isMasterBuilder())
                || (data.isMasterBuilder() && !permissions.mbsBypass);
    }

    /**
     * Protection-oriented listener for block place events
     * @implNote Intends to cockblock shit from like WorldEdit
     * @param event BlockPlaceEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();

        if (world.equals(player.getWorld()) && doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    /**
     * Protection-oriented listener for block break events
     * @implNote Intends to cockblock shit from like WorldEdit
     * @param event BlockBreakEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();

        if (world.equals(player.getWorld()) && doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    /**
     * Protection-oriented listener for interaction events
     * @param event PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (world.equals(player.getWorld()) && doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    /**
     * Protection-oriented listener for interaction events
     * @param event PlayerInteractEntityEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEntity(PlayerInteractEntityEvent event)
    {
        Player player = event.getPlayer();

        if (world.equals(player.getWorld()) && doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    /**
     * Protection-oriented listener for damage events
     * @param event EntityDamageByEntityEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageEntity(EntityDamageByEntityEvent event)
    {
        if (world.equals(event.getEntity().getWorld()) && event.getEntity() instanceof Player player && doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    /**
     * Protection-oriented listener for command events
     * @param event PlayerCommandPreprocessEvent
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        String[] cmdString = event.getMessage().substring(1).toLowerCase().split(" ");

        if (world.equals(event.getPlayer().getWorld()) && doRestrict(event.getPlayer()) && CustomWorldManager.BLOCKED_WORLD_COMMANDS.contains(cmdString[0]))
        {
            event.getPlayer().sendMessage(Component.text("That command is blocked while you are in this world.", TextColor.color(0xff5555)));
            event.setCancelled(true);
        }
    }

    /**
     * Changes how the weather works per world.
     * @param event PlayerCommandPreprocessEvent
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(ThunderChangeEvent event)
    {
        if (world.equals(event.getWorld()) && settings.weatherDisabled)
            event.setCancelled(true);
    }

    /**
     * Changes how the weather works per world.
     * @param event PlayerCommandPreprocessEvent
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event)
    {
        if (world.equals(event.getWorld()) && settings.weatherDisabled)
            event.setCancelled(true);
    }

    /**
     * <p>Send a player to this world.</p>
     * @param player Player
     */
    public void sendToWorld(Player player)
    {
        try
        {
            FUtil.fixCommandVoid(player);
            PaperLib.teleportAsync(player, getWorld().getSpawnLocation());
        }
        catch (Exception ex)
        {
            player.sendMessage(ex.getMessage());
        }
    }

    @Getter
    public static class Command
    {
        private boolean enabled;
        private String description;
        private String usage;
        private String aliases = "";
        private Rank rank;
        private SourceType type;
        private boolean blockingHostConsole;
        private int cooldown = 0;
    }

    public static class Flags
    {
        private boolean allowAnimals;
        private boolean allowMonsters;
    }

    public static class Generation
    {
        private World.Environment environment = World.Environment.NORMAL;
        private boolean generateStructures = true;
        private WorldType type = WorldType.NORMAL;
        private boolean useCleanroomGeneration = false;
        private String cleanroomParameters = CustomWorldManager.FALLBACK_CLEANROOM_PARAMETERS;
    }

    public static class Permissions
    {
        private Rank minModifyRank;
        public boolean mbsBypass;
        public boolean mbRequired;
    }

    @Data
    public static class Settings
    {
        private boolean weatherDisabled;
        private boolean protectedFromRO;
    }
}
