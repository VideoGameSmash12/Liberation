package me.totalfreedom.totalfreedommod.command;

import java.util.SplittableRandom;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Makes random sounds.", usage = "/<command>")
public class Command_deafen extends FreedomCommand
{

    public static final double STEPS = 10.0;
    private static final SplittableRandom random = new SplittableRandom();

    private static Location randomOffset(Location a)
    {
        return a.clone().add(randomDoubleRange() * 5.0, randomDoubleRange() * 5.0, randomDoubleRange() * 5.0);
    }

    private static Double randomDoubleRange()
    {
        return -1.0 + (random.nextDouble() * ((1.0 - -1.0) + 1.0));
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        {
            for (double percent = 0.0; percent <= 1.0; percent += (1.0 / STEPS))
            {
                final float pitch = (float)(percent * 2.0);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        playerSender.playSound(randomOffset(playerSender.getLocation()), Sound.values()[random.nextInt(Sound.values().length)], 100.0f, pitch);
                    }
                }.runTaskLater(plugin, Math.round(20.0 * percent * 2.0));
            }
        }

        return true;
    }
}