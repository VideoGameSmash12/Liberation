package me.totalfreedom.totalfreedommod.rank;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public enum Title implements Displayable
{

    MASTER_BUILDER("a", "Master Builder", ChatColor.DARK_AQUA, org.bukkit.ChatColor.DARK_AQUA, "MB", true, true),
    VERIFIED_STAFF("a", "Verified Staff Member", ChatColor.LIGHT_PURPLE, org.bukkit.ChatColor.LIGHT_PURPLE, "VS", false, true),
    EXECUTIVE("an", "Executive", ChatColor.RED, org.bukkit.ChatColor.RED, "Exec", true, true),
    DEVELOPER("a", "Developer", ChatColor.DARK_PURPLE, org.bukkit.ChatColor.DARK_PURPLE, "Dev", true, true),
    OWNER("the", "Owner", ChatColor.of("#ff0000"), org.bukkit.ChatColor.DARK_RED, "Owner", true, true);

    @Getter
    private final String article;
    @Getter
    private final String name;
    @Getter
    private final String abbr;
    @Getter
    private final String tag;
    @Getter
    private final String coloredTag;
    @Getter
    private final ChatColor color;
    @Getter
    private final org.bukkit.ChatColor teamColor;
    @Getter
    private final boolean hasTeam;
    private final boolean hasDefaultLoginMessage;

    Title(String article, String name, ChatColor color, org.bukkit.ChatColor teamColor, String tag, Boolean hasTeam, Boolean hasDefaultLoginMessage)
    {
        this.article = article;
        this.name = name;
        this.coloredTag = ChatColor.DARK_GRAY + "[" + color + tag + ChatColor.DARK_GRAY + "]" + color;
        this.abbr = tag;
        this.tag = "[" + tag + "]";
        this.color = color;
        this.teamColor = teamColor;
        this.hasTeam = hasTeam;
        this.hasDefaultLoginMessage = hasDefaultLoginMessage;
    }

    @Override
    public String getColoredName()
    {
        return color + name;
    }

    @Override
    public boolean hasTeam()
    {
        return hasTeam;
    }

    @Override
    public boolean hasDefaultLoginMessage()
    {
        return hasDefaultLoginMessage;
    }

    @Override
    public String getColoredLoginMessage()
    {
        return article + " " + color + name;
    }
}