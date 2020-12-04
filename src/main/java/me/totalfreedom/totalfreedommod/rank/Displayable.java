package me.totalfreedom.totalfreedommod.rank;

import net.md_5.bungee.api.ChatColor;

public interface Displayable
{

    String getArticle();

    String getName();

    String getTag();

    String getAbbr();

    ChatColor getColor();

    org.bukkit.ChatColor getTeamColor();

    String getColoredName();

    String getColoredTag();

    String getColoredLoginMessage();

    boolean hasTeam();

    boolean hasDefaultLoginMessage();

}