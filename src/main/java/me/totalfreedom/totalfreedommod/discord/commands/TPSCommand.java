package me.totalfreedom.totalfreedommod.discord.commands;

import me.totalfreedom.totalfreedommod.discord.command.DiscordCommand;
import me.totalfreedom.totalfreedommod.discord.command.DiscordCommandImpl;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

public class TPSCommand extends DiscordCommandImpl
{
    @Override
    public String getCommandName()
    {
        return "tps";
    }

    @Override
    public String getDescription()
    {
        return "Lag information regarding the server.";
    }

    @Override
    public String getCategory()
    {
        return "Server Commands";
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("lag");
    }

    @Override
    public boolean isAdmin()
    {
        return false;
    }

    @Override
    public MessageBuilder execute(Member member, List<String> args)
    {
        final EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Server lag information");
        builder.addField("TPS", String.valueOf(Math.round(FUtil.getMeanAverageDouble(Bukkit.getServer().getTPS()))), false);
        builder.addField("Uptime", FUtil.getUptime(), false);
        builder.addField("Maximum Memory", Math.ceil(FUtil.getMaxMem()) + " MB", false);
        builder.addField("Allocated Memory", Math.ceil(FUtil.getTotalMem()) + " MB", false);
        builder.addField("Free Memory", Math.ceil(FUtil.getFreeMem()) + " MB", false);

        return new MessageBuilder().setEmbed(builder.build());
    }
}
