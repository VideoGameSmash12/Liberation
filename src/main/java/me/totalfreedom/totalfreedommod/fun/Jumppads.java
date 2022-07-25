package me.totalfreedom.totalfreedommod.fun;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.util.Groups;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Jumppads extends FreedomService
{

    public static final double DAMPING_COEFFICIENT = 0.8;
    //
    private final Map<Player, Boolean> pushMap = Maps.newHashMap();
    //
    private final double strength = 1 + 0.1F;
    public HashMap<Player, JumpPadMode> players = new HashMap<>();

    public static double getDampingCoefficient()
    {
        return DAMPING_COEFFICIENT;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (!players.containsKey(event.getPlayer()))
        {
            players.put(event.getPlayer(), JumpPadMode.OFF);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (players.get(event.getPlayer()) == JumpPadMode.OFF)
        {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = Objects.requireNonNull(event.getTo()).getBlock();
        final Vector velocity = player.getVelocity().clone();

        if (players.get(event.getPlayer()) == JumpPadMode.MADGEEK)
        {
            Boolean canPush = pushMap.get(player);
            if (canPush == null)
            {
                canPush = true;
            }
            if (Tag.WOOL.getValues().contains(block.getRelative(0, -1, 0).getType()))
            {
                if (canPush)
                {
                    velocity.multiply(strength + 0.85).multiply(-1.0);
                }
                canPush = false;
            }
            else
            {
                canPush = true;
            }
            pushMap.put(player, canPush);
        }
        else
        {
            if (Tag.WOOL.getValues().contains(block.getRelative(0, -1, 0).getType()))
            {
                velocity.add(new Vector(0.0, strength, 0.0));
            }

            if (players.get(event.getPlayer()) == JumpPadMode.NORMAL_AND_SIDEWAYS)
            {
                if (Tag.WOOL.getValues().contains(block.getRelative(1, 0, 0).getType()))
                {
                    velocity.add(new Vector(-DAMPING_COEFFICIENT * strength, 0.0, 0.0));
                }

                if (Tag.WOOL.getValues().contains(block.getRelative(-1, 0, 0).getType()))
                {
                    velocity.add(new Vector(DAMPING_COEFFICIENT * strength, 0.0, 0.0));
                }

                if (Tag.WOOL.getValues().contains(block.getRelative(0, 0, 1).getType()))
                {
                    velocity.add(new Vector(0.0, 0.0, -DAMPING_COEFFICIENT * strength));
                }

                if (Tag.WOOL.getValues().contains(block.getRelative(0, 0, -1).getType()))
                {
                    velocity.add(new Vector(0.0, 0.0, DAMPING_COEFFICIENT * strength));
                }
            }
        }

        if (!player.getVelocity().equals(velocity))
        {
            player.setFallDistance(0.0f);
            player.setVelocity(velocity);
        }
    }

    public Map<Player, Boolean> getPushMap()
    {
        return pushMap;
    }

    public double getStrength()
    {
        return strength;
    }

    public HashMap<Player, JumpPadMode> getPlayers()
    {
        return players;
    }

    public void setPlayers(HashMap<Player, JumpPadMode> players)
    {
        this.players = players;
    }

    public enum JumpPadMode
    {

        OFF(false), NORMAL_AND_SIDEWAYS(true), MADGEEK(true);
        private final boolean on;

        JumpPadMode(boolean on)
        {
            this.on = on;
        }

        public boolean isOn()
        {
            return on;
        }
    }
}
