package me.totalfreedom.totalfreedommod.rank;

import net.md_5.bungee.api.ChatColor;

public enum Title implements Displayable
{

    MASTER_BUILDER("a", "Master Builder", "Master Builders", ChatColor.DARK_AQUA, org.bukkit.ChatColor.DARK_AQUA, "MB", true, true),
    EXECUTIVE("an", "Executive", "Executives", ChatColor.RED, org.bukkit.ChatColor.RED, "Exec", true, true),
    ASSTEXEC("an", "Assistant Executive", "Assistant Executives", ChatColor.RED, org.bukkit.ChatColor.RED, "Asst Exec", true, true),
    DEVELOPER("a", "Developer", "Developers", ChatColor.DARK_PURPLE, org.bukkit.ChatColor.DARK_PURPLE, "Dev", true, true),
    OWNER("the", "Owner", "Owners", ChatColor.DARK_RED, org.bukkit.ChatColor.DARK_RED, "Owner", true, true);


    private final String article;

    private final String name;

    private final String abbr;
    private final String plural;

    private final String tag;

    private final String coloredTag;

    private final ChatColor color;

    private final org.bukkit.ChatColor teamColor;

    private final boolean hasTeam;
    private final boolean hasDefaultLoginMessage;

    Title(String article, String name, String plural, ChatColor color, org.bukkit.ChatColor teamColor, String tag, Boolean hasTeam, Boolean hasDefaultLoginMessage)
    {
        this.article = article;
        this.name = name;
        this.plural = plural;
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

    @Override
    public String getArticle()
    {
        return article;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getAbbr()
    {
        return abbr;
    }

    @Override
    public String getPlural()
    {
        return plural;
    }

    @Override
    public String getTag()
    {
        return tag;
    }

    @Override
    public String getColoredTag()
    {
        return coloredTag;
    }

    @Override
    public ChatColor getColor()
    {
        return color;
    }

    @Override
    public org.bukkit.ChatColor getTeamColor()
    {
        return teamColor;
    }
}