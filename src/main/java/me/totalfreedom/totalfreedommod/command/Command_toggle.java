package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.GameRuleHandler;
import me.totalfreedom.totalfreedommod.LoginProcess;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Toggles TotalFreedomMod settings", usage = "/<command> [option] [value] [value]")
public class Command_toggle extends FreedomCommand
{
    private final List<String> toggles = Arrays.asList(
            "waterplace", "fireplace", "lavaplace", "fluidspread", "lavadmg", "firespread", "frostwalk",
            "firework", "prelog", "lockdown", "petprotect", "entitywipe", "nonuke [range] [count]",
            "explosives [radius]", "unsafeenchs", "bells", "armorstands", "masterblocks", "grindstones",
            "jukeboxes", "spawners", "4chan", "beehives", "respawnanchors", "autotp", "autoclear", "minecarts", "mp44",
            "landmines", "tossmob", "gravity", "chat");

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        switch (args.length == 0 ? "list" : args[0].toLowerCase())
        {
            case "waterplace" -> toggle("Water placement is", ConfigEntry.ALLOW_WATER_PLACE);
            case "frostwalk" -> toggle("Frost walker enchantment is", ConfigEntry.ALLOW_FROSTWALKER);
            case "fireplace" -> toggle("Fire placement is", ConfigEntry.ALLOW_FIRE_PLACE);
            case "lavaplace" -> toggle("Lava placement is", ConfigEntry.ALLOW_LAVA_PLACE);
            case "fluidspread" -> toggle("Fluid spread is", ConfigEntry.ALLOW_FLUID_SPREAD);
            case "lavadmg" -> toggle("Lava damage is", ConfigEntry.ALLOW_LAVA_DAMAGE);
            case "firespread" ->
            {
                toggle("Fire spread is", ConfigEntry.ALLOW_FIRE_SPREAD);
                plugin.gr.setGameRule(GameRuleHandler.GameRule.DO_FIRE_TICK, ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean());
            }
            case "prelog" -> toggle("Command prelogging is", ConfigEntry.ENABLE_PREPROCESS_LOG);
            case "lockdown" ->
            {
                boolean active = !LoginProcess.isLockdownEnabled();
                LoginProcess.setLockdownEnabled(active);
                FUtil.adminAction(sender.getName(), (active ? "A" : "De-a") + "ctivating server lockdown", true);
            }
            case "petprotect" -> toggle("Tamed pet protection is", ConfigEntry.ENABLE_PET_PROTECT);
            case "entitywipe" -> toggle("Automatic entity wiping is", ConfigEntry.AUTO_ENTITY_WIPE);
            case "firework" -> toggle("Firework explosion is", ConfigEntry.ALLOW_FIREWORK_EXPLOSION);
            case "nonuke" ->
            {
                if (args.length >= 2)
                {
                    try
                    {
                        ConfigEntry.NUKE_MONITOR_RANGE.setDouble(Math.max(1.0, Math.min(500.0, Double.parseDouble(args[1]))));
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("The input provided is not a valid integer.");
                        return true;
                    }
                }

                if (args.length >= 3)
                {
                    try
                    {
                        ConfigEntry.NUKE_MONITOR_COUNT_BREAK.setInteger(Math.max(1, Math.min(500, Integer.parseInt(args[2]))));
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("The input provided is not a valid integer.");
                        return true;
                    }
                }

                toggle("Nuke monitor is", ConfigEntry.NUKE_MONITOR_ENABLED);

                if (ConfigEntry.NUKE_MONITOR_ENABLED.getBoolean())
                {
                    msg("Anti-freecam range is set to " + ConfigEntry.NUKE_MONITOR_RANGE.getDouble() + " blocks.");
                    msg("Block throttle rate is set to " + ConfigEntry.NUKE_MONITOR_COUNT_BREAK.getInteger() + " blocks destroyed per 5 seconds.");
                }
            }
            case "explosives" ->
            {
                if (args.length == 2)
                {
                    try
                    {
                        ConfigEntry.EXPLOSIVE_RADIUS.setDouble(Math.max(1.0, Math.min(30.0, Double.parseDouble(args[1]))));
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("The input provided is not a valid integer.");
                        return true;
                    }
                }

                toggle("Explosions are", ConfigEntry.ALLOW_EXPLOSIONS);

                if (ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
                {
                    msg("Radius set to " + ConfigEntry.EXPLOSIVE_RADIUS.getDouble());
                }
            }
            case "unsafeenchs" -> toggle("Unsafe enchantments are", ConfigEntry.ALLOW_UNSAFE_ENCHANTMENTS);
            case "bells" -> toggle("The ringing of bells is", ConfigEntry.ALLOW_BELLS);
            case "armorstands" -> toggle("The placement of armor stands is", ConfigEntry.ALLOW_ARMOR_STANDS);
            case "masterblocks" -> toggle("Master blocks are", ConfigEntry.ALLOW_MASTERBLOCKS);
            case "grindstones" -> toggle("Grindstones are", ConfigEntry.ALLOW_GRINDSTONES);
            case "jukeboxes" -> toggle("Jukeboxes are", ConfigEntry.ALLOW_JUKEBOXES);
            case "spawners" -> toggle("Spawners are", ConfigEntry.ALLOW_SPAWNERS);
            case "4chan" -> toggle("4chan mode is", ConfigEntry.FOURCHAN_ENABLED);
            case "beehives" -> toggle("Beehives are", ConfigEntry.ALLOW_BEEHIVES);
            case "respawnanchors" -> toggle("Respawn anchors are", ConfigEntry.ALLOW_RESPAWN_ANCHORS);
            case "autotp" -> toggle("Teleportation on join is", ConfigEntry.AUTO_TP);
            case "autoclear" -> toggle("Clearing inventories on join is", ConfigEntry.AUTO_CLEAR);
            case "minecarts" -> toggle("Minecarts are", ConfigEntry.ALLOW_MINECARTS);
            case "landmines" -> toggle("Landmines are", ConfigEntry.LANDMINES_ENABLED);
            case "mp44" -> toggle("MP44 is", ConfigEntry.MP44_ENABLED);
            case "tossmob" -> toggle("Tossmob is", ConfigEntry.TOSSMOB_ENABLED);
            case "gravity" -> toggle("Block gravity is", ConfigEntry.ALLOW_GRAVITY);
            case "chat" ->
            {
                FUtil.adminAction(sender.getName(), (ConfigEntry.TOGGLE_CHAT.getBoolean() ? "Dis" : "En") + "abling global chat for all non-admins", true);
                toggle("The global chat is", ConfigEntry.TOGGLE_CHAT);
            }
            default ->
            {
                msg("Available toggles: ");
                for (String toggle : toggles)
                {
                    msg("- " + toggle);
                }
                return false;
            }
        }
        return true;
    }

    private void toggle(final String name, final ConfigEntry entry)
    {
        msg(name + " now " + (entry.setBoolean(!entry.getBoolean()) ? "enabled." : "disabled."));
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!plugin.al.isAdmin(sender))
        {
            return Collections.emptyList();
        }

        if (args.length == 1)
        {
            return Arrays.asList(
                    "waterplace", "fireplace", "lavaplace", "fluidspread", "lavadmg", "firespread", "frostwalk",
                    "firework", "prelog", "lockdown", "petprotect", "entitywipe", "nonuke", "explosives", "unsafeenchs",
                    "bells", "armorstands", "structureblocks", "jigsaws", "grindstones", "jukeboxes", "spawners", "4chan", "beehives",
                    "respawnanchors", "autotp", "autoclear", "minecarts", "mp44", "landmines", "tossmob", "gravity");
        }
        return Collections.emptyList();
    }
}