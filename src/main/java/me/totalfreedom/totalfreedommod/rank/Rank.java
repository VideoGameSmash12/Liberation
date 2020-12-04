package me.totalfreedom.totalfreedommod.rank;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public enum Rank implements Displayable
{
    IMPOSTOR("an", "Impostor", Type.PLAYER, "Imp", ChatColor.YELLOW, null, false, false),
    NON_OP("a", "Non-Op", Type.PLAYER, "", ChatColor.WHITE, null, false, false),
    OP("an", "Operator", Type.PLAYER, "OP", ChatColor.GREEN, null, false, false),
    ADMIN("an", "Admin", Type.ADMIN, "Admin", ChatColor.DARK_GREEN, org.bukkit.ChatColor.DARK_GREEN, true, true),
    SENIOR_ADMIN("a", "Senior Admin", Type.ADMIN, "SrA", ChatColor.GOLD, org.bukkit.ChatColor.GOLD, true, true),
    ADMIN_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE, null, false, false),
    SENIOR_CONSOLE("the", "Console", Type.ADMIN_CONSOLE, "Console", ChatColor.DARK_PURPLE, null, false, false);
    @Getter
    private final Type type;
    @Getter
    private final String name;
    @Getter
    private final String abbr;
    @Getter
    private final String article;
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
    @Getter
    private final boolean hasDefaultLoginMessage;

    Rank(String article, String name, Type type, String abbr, ChatColor color, org.bukkit.ChatColor teamColor, Boolean hasTeam, Boolean hasDefaultLoginMessage)
    {
        this.type = type;
        this.name = name;
        this.abbr = abbr;
        this.article = article;
        this.tag = abbr.isEmpty() ? "" : "[" + abbr + "]";
        this.coloredTag = abbr.isEmpty() ? "" : ChatColor.DARK_GRAY + "[" + color + abbr + ChatColor.DARK_GRAY + "]" + color;
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
    public String getColoredLoginMessage()
    {
        return article + " " + color + name;
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
    public String getAbbr()
    {
        return abbr;
    }

    public boolean isConsole()
    {
        return getType() == Type.ADMIN_CONSOLE;
    }

    public int getLevel()
    {
        return ordinal();
    }

    public boolean isAtLeast(Rank rank)
    {
        if (getLevel() < rank.getLevel())
        {
            return false;
        }

        if (!hasConsoleVariant() || !rank.hasConsoleVariant())
        {
            return true;
        }

        return getConsoleVariant().getLevel() >= rank.getConsoleVariant().getLevel();
    }

    public boolean isAdmin()
    {
        return getType() == Type.ADMIN || getType() == Type.ADMIN_CONSOLE;
    }

    public boolean hasConsoleVariant()
    {
        return getConsoleVariant() != null;
    }

    public Rank getConsoleVariant()
    {
        switch (this)
        {
            case ADMIN:
            case ADMIN_CONSOLE:
                return ADMIN_CONSOLE;
            case SENIOR_ADMIN:
            case SENIOR_CONSOLE:
                return SENIOR_CONSOLE;
            default:
                return null;
        }
    }

    public static Rank findRank(String string)
    {
        try
        {
            return Rank.valueOf(string.toUpperCase());
        }
        catch (Exception ignored)
        {
        }

        return Rank.NON_OP;
    }

    public enum Type
    {
        PLAYER,
        ADMIN,
        ADMIN_CONSOLE;

        public boolean isAdmin()
        {
            return this != PLAYER;
        }
    }
}