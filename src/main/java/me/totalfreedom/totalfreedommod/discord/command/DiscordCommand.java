package me.totalfreedom.totalfreedommod.discord.command;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public interface DiscordCommand
{
    String getCommandName();

    String getDescription();

    String getCategory();

    List<String> getAliases();

    boolean isAdmin();

    boolean canExecute(Member member);

    MessageBuilder execute(Member member, List<String> args);
}
