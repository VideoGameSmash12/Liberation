package me.totalfreedom.totalfreedommod.blocking;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.Groups;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractBlocker extends FreedomService
{
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        switch (event.getAction())
        {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
            {
                handleRightClick(event);
                break;
            }

            default:
            {
                // Do nothing
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRightClickBell(PlayerInteractEvent event)
    {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.BELL) && !ConfigEntry.ALLOW_BELLS.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event)
    {
        Player player = event.getPlayer();
        if (Groups.EXPLOSIVE_BED_BIOMES.contains(event.getBed().getBiome()))
        {
            player.sendMessage(ChatColor.RED + "You may not sleep here.");
            event.setCancelled(true);
        }
    }

    private void handleRightClick(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null)
        {
            return;
        }

        if (clickedBlock.getType() == Material.RESPAWN_ANCHOR && !ConfigEntry.ALLOW_RESPAWN_ANCHORS.getBoolean())
        {
            event.setCancelled(true);
            return;
        }

        if (Groups.SPAWN_EGGS.contains(event.getMaterial()))
        {
            event.setCancelled(true);
            EntityType eggType = null;
            try
            {
                Material mat = event.getMaterial();
                if (mat == Material.MOOSHROOM_SPAWN_EGG)
                {
                    eggType = EntityType.MUSHROOM_COW;
                }
                else
                {
                    eggType = EntityType.valueOf(mat.name().substring(0, mat.name().length() - 10));
                }
            }
            catch (IllegalArgumentException ignored)
            {
                //
            }
            if (eggType != null)
            {
                clickedBlock.getWorld().spawnEntity(clickedBlock.getLocation().add(event.getBlockFace().getDirection()).add(0.5, 0.5, 0.5), eggType);
            }
            return;
        }

        switch (event.getMaterial())
        {
            case WATER_BUCKET:
            {
                if (plugin.al.isAdmin(player) || ConfigEntry.ALLOW_WATER_PLACE.getBoolean())
                {
                    break;
                }

                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GRAY + "Water buckets are currently disabled.");
                event.setCancelled(true);
                break;
            }

            case LAVA_BUCKET:
            {
                if (plugin.al.isAdmin(player) || ConfigEntry.ALLOW_LAVA_PLACE.getBoolean())
                {
                    break;
                }

                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GRAY + "Lava buckets are currently disabled.");
                event.setCancelled(true);
                break;
            }

            case TNT_MINECART:
            {
                if (ConfigEntry.ALLOW_TNT_MINECARTS.getBoolean())
                {
                    break;
                }

                player.getInventory().clear(player.getInventory().getHeldItemSlot());
                player.sendMessage(ChatColor.GRAY + "TNT minecarts are currently disabled.");
                event.setCancelled(true);
                break;
            }

            case ARMOR_STAND:
            {
                if (ConfigEntry.ALLOW_ARMOR_STANDS.getBoolean())
                {
                    break;
                }

                player.getInventory().clear(player.getInventory().getHeldItemSlot());
                player.sendMessage(ChatColor.GRAY + "Armor stands are currently disabled.");
                event.setCancelled(true);
                break;
            }
            case MINECART:
            {
                if (ConfigEntry.ALLOW_MINECARTS.getBoolean())
                {
                    break;
                }

                player.getInventory().clear(player.getInventory().getHeldItemSlot());
                player.sendMessage(ChatColor.GRAY + "Minecarts are currently disabled.");
                event.setCancelled(true);
                break;
            }
            case WRITTEN_BOOK:
            {
                player.getInventory().clear(player.getInventory().getHeldItemSlot());
                player.sendMessage(ChatColor.GRAY + "Books are currently disabled.");
                event.setCancelled(true);
                break;
            }
            default:
            {
                // Do nothing
                break;
            }
        }
    }
}