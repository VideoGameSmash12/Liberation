package me.totalfreedom.totalfreedommod.blocking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
/* TODO This will have to be changed from com.github.atlasmediagroup.scissors to me.totalfreedom.scissors when we migrate to 1.19 */
import com.github.atlasmediagroup.scissors.event.block.MasterBlockFireEvent;
import io.papermc.paper.event.player.PlayerSignCommandPreprocessEvent;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.util.Groups;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EventBlocker extends FreedomService
{
    /**
     * /@EventHandler(priority = EventPriority.HIGH)
     * /public void onBlockRedstone(BlockRedstoneEvent event)
     * /{
     * /    if (!ConfigEntry.ALLOW_REDSTONE.getBoolean())
     * /    {
     * /        event.setNewCurrent(0);
     * /    }
     * /}
     **/

    // TODO: Revert back to old redstone block system when (or if) it is fixed in Bukkit, Spigot or Paper.
    private final ArrayList<Material> redstoneBlocks = new ArrayList<>(Arrays.asList(Material.REDSTONE, Material.DISPENSER, Material.DROPPER, Material.REDSTONE_LAMP));

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBurn(BlockBurnEvent event)
    {
        if (!ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        if (!ConfigEntry.ALLOW_FIRE_PLACE.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockFromTo(BlockFromToEvent event)
    {
        if (!ConfigEntry.ALLOW_FLUID_SPREAD.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event)
    {
        if (!ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            event.blockList().clear();
            return;
        }

        event.setYield(0.0F);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExplosionPrime(ExplosionPrimeEvent event)
    {
        if (!ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            event.setCancelled(true);
            return;
        }

        event.setRadius(ConfigEntry.EXPLOSIVE_RADIUS.getDouble().floatValue());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockExplode(BlockExplodeEvent event)
    {
        if (!ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            event.setCancelled(true);
            return;
        }

        event.setYield(ConfigEntry.EXPLOSIVE_RADIUS.getDouble().floatValue());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMasterBlockFire(MasterBlockFireEvent event)
    {
        if (!ConfigEntry.ALLOW_MASTERBLOCKS.getBoolean())
        {
            event.setCancelled(true);
            event.getAt().getBlock().setType(Material.CAKE);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityCombust(EntityCombustEvent event)
    {
        if (!ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (ConfigEntry.AUTO_ENTITY_WIPE.getBoolean())
        {
            event.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event)
    {
        if ((event.getCause() == EntityDamageEvent.DamageCause.LAVA)
                && !ConfigEntry.ALLOW_LAVA_DAMAGE.getBoolean())
        {
            event.setCancelled(true);
            return;
        }

        if (ConfigEntry.ENABLE_PET_PROTECT.getBoolean())
        {
            Entity entity = event.getEntity();
            if (entity instanceof Tameable)
            {
                if (((Tameable) entity).isTamed())
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        if (!plugin.al.isAdmin(event.getPlayer()))
        {
            event.setCancelled(true);
        }

        if (!ConfigEntry.AUTO_ENTITY_WIPE.getBoolean())
        {
            return;
        }

        if (event.getPlayer().getWorld().getEntities().size() > 750)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLeavesDecay(LeavesDecayEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockGrowth(BlockGrowEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFireworkExplode(FireworkExplodeEvent event)
    {
        if (!ConfigEntry.ALLOW_FIREWORK_EXPLOSION.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPistonRetract(BlockPistonRetractEvent event)
    {
        if (!ConfigEntry.ALLOW_REDSTONE.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPistonExtend(BlockPistonExtendEvent event)
    {
        if (!ConfigEntry.ALLOW_REDSTONE.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event)
    {
        if (!ConfigEntry.ALLOW_GRAVITY.getBoolean() && event.getEntity() instanceof FallingBlock)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        if (!ConfigEntry.ALLOW_REDSTONE.getBoolean())
        {
            // Check if the block is involved with redstone.
            if (event.getBlock().getBlockData() instanceof AnaloguePowerable || event.getBlock().getBlockData() instanceof Powerable || redstoneBlocks.contains(event.getBlock().getType()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        double maxHealth = Objects.requireNonNull(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        if (maxHealth < 1)
        {
            for (AttributeModifier attributeModifier : Objects.requireNonNull(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getModifiers())
            {
                Objects.requireNonNull(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).removeModifier(attributeModifier);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockDispense(BlockDispenseEvent event)
    {
        List<Material> banned = Arrays.asList(Material.TNT_MINECART, Material.MINECART);
        if (Groups.SPAWN_EGGS.contains(event.getItem().getType()) || banned.contains(event.getItem().getType()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        FUtil.fixCommandVoid(event.getEntity());
        event.setDeathMessage(event.getDeathMessage());
    }

    @EventHandler
    public void onSignInteract(PlayerSignCommandPreprocessEvent event)
    {
        event.setCancelled(true);
    }
}
