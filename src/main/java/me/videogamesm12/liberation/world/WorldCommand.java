package me.videogamesm12.liberation.world;

import me.totalfreedom.totalfreedommod.command.FreedomCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCommand extends FreedomCommand
{
    private final CustomWorld world;

    public WorldCommand(CustomWorld world)
    {
        super(world.getName().replace(" ", "_"), world.getCommand().getDescription(), world.getCommand().getUsage(),
                world.getCommand().getAliases(), world.getCommand().getRank(), world.getCommand().getType(),
                world.getCommand().isBlockingHostConsole(), world.getCommand().getCooldown());

        this.world = world;
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        world.sendToWorld(playerSender);
        return true;
    }
}
