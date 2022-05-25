package me.totalfreedom.totalfreedommod.discord.commands;

import me.totalfreedom.totalfreedommod.discord.Discord;
import me.totalfreedom.totalfreedommod.discord.command.DiscordCommand;
import me.totalfreedom.totalfreedommod.discord.command.DiscordCommandImpl;
import me.totalfreedom.totalfreedommod.discord.command.DiscordCommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpCommand extends DiscordCommandImpl
{
    @Override
    public String getCommandName()
    {
        return "help";
    }

    @Override
    public String getDescription()
    {
        return "Displays the help command";
    }

    @Override
    public String getCategory()
    {
        return "Help";
    }

    @Override
    public List<String> getAliases()
    {
        return List.of("cmds", "commands", "elp");
    }

    @Override
    public boolean isAdmin()
    {
        return false;
    }

    @Override
    public MessageBuilder execute(Member member, List<String> args)
    {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setTitle("Help Command");

        final Map<String, List<DiscordCommand>> commandCategories = new HashMap<>();

        for (DiscordCommand command : Discord.DISCORD_COMMAND_MANAGER.commands)
        {
            if (!commandCategories.containsKey(command.getCategory()))
            {
                commandCategories.put(command.getCategory(), new ArrayList<>(List.of(command)));
            }
            else
            {
                commandCategories.get(command.getCategory()).add(command);
            }
        }

        for (Map.Entry<String, List<DiscordCommand>> entry : commandCategories.entrySet())
        {
            final String category = entry.getKey();
            final List<DiscordCommand> commands = entry.getValue();
            final StringBuilder fieldValue = new StringBuilder();

            for (DiscordCommand command : commands)
            {
                fieldValue.append("**").append(DiscordCommandManager.PREFIX).append(command.getCommandName()).append("** - ").append(command.getDescription()).append("\n");
            }

            embedBuilder.addField(category, fieldValue.toString().trim(), false);
        }

        return new MessageBuilder().setEmbed(embedBuilder.build());
    }
}
