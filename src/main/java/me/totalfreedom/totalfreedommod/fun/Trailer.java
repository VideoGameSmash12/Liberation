package me.totalfreedom.totalfreedommod.fun;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.UUID;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.shop.ShopItem;
import me.totalfreedom.totalfreedommod.util.Groups;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class Trailer extends FreedomService
{
    private final SplittableRandom random = new SplittableRandom();
    private final Set<UUID> trailPlayers = new HashSet<>(); // player UUID

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        /* Doesn't continue any further if...
         * - The trail list is empty
         * - The player doesn't have their trail enabled in the first place
         * - The player doesn't have the trail item in the shop at all
         * - The player doesn't have permission to modify blocks in their current world
         */
        if (trailPlayers.isEmpty()
                || !trailPlayers.contains(event.getPlayer().getUniqueId())
                || !plugin.pl.getData(event.getPlayer()).hasItem(ShopItem.RAINBOW_TRAIL)
                || plugin.wr.doRestrict(event.getPlayer())
                || !plugin.wgb.canEditCurrentWorld(event.getPlayer()))
        {
            return;
        }

        Block fromBlock = event.getFrom().getBlock();
        if (!fromBlock.isEmpty())
        {
            return;
        }

        Block toBlock = Objects.requireNonNull(event.getTo()).getBlock();
        if (fromBlock.equals(toBlock))
        {
            return;
        }

        fromBlock.setType(Groups.WOOL_COLORS.get(random.nextInt(Groups.WOOL_COLORS.size())));
        BlockData data = fromBlock.getBlockData();
        Material material = Material.getMaterial(String.valueOf(fromBlock.getType()));
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location trail_pos;
                trail_pos = new Location(event.getPlayer().getWorld(), fromBlock.getX() + x, fromBlock.getY(), fromBlock.getZ() + z);
                if (trailPlayers.contains(event.getPlayer().getUniqueId()) && plugin.cpb.isEnabled())
                {
                    plugin.cpb.getCoreProtectAPI().logPlacement(event.getPlayer().getName(), trail_pos, material, data);
                }
            }
        }
    }

    public void remove(Player player)
    {
        trailPlayers.remove(player.getUniqueId());
    }

    public void add(Player player)
    {
        trailPlayers.add(player.getUniqueId());
    }

    public boolean contains(Player player)
    {
        return trailPlayers.contains(player.getUniqueId());
    }
}
