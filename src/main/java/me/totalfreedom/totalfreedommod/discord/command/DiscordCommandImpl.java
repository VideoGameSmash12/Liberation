package me.totalfreedom.totalfreedommod.discord.command;

import net.dv8tion.jda.api.entities.Member;

public abstract class DiscordCommandImpl implements DiscordCommand
{
    @Override
    public boolean canExecute(Member member)
    {
        //return !isAdmin() || member.getRoles().stream().filter((role -> role.getName().toLowerCase().contains("admin") && !role.getName().toLowerCase().contains("discord"))).toList().size() > 0;
        return !isAdmin();
    }
}
