package me.totalfreedom.totalfreedommod.discord.commands;

import me.totalfreedom.totalfreedommod.discord.command.DiscordCommandImpl;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class UptimeCommand extends DiscordCommandImpl
{
    @Override
    public String getCommandName()
    {
        return "uptime";
    }

    @Override
    public String getDescription()
    {
        return "Returns the uptime of the host.";
    }

    @Override
    public String getCategory()
    {
        return "Server Commands";
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.emptyList();
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

        try
        {
            final Process uptimeProcess = Runtime.getRuntime().exec(new String[]{"uptime"});
            BufferedReader input = new BufferedReader(new InputStreamReader(uptimeProcess.getInputStream()));
            String line = input.readLine();

            if (line != null)
            {
                embedBuilder.setTitle("Host Uptime Information");
                embedBuilder.setDescription(line.trim());
            }
            else
            {
                throw new IllegalStateException("No output from uptime command.");
            }
        }
        catch (Exception e)
        {
            FLog.warning("Error while executing uptime Discord command");
            e.printStackTrace();
            embedBuilder.setTitle("Command error");
            embedBuilder.setColor(Color.RED);
            embedBuilder.setDescription("Something went wrong");
        }

        return new MessageBuilder().setEmbed(embedBuilder.build());
    }
}
