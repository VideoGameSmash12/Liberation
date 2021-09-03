package me.totalfreedom.totalfreedommod.shop;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum ShopItem
{
    GRAPPLING_HOOK("Grappling Hook", Material.FISHING_ROD, 10, ConfigEntry.SHOP_PRICES_GRAPPLING_HOOK, ChatColor.GREEN, "grapplingHook", "/grapplinghook"),
    LIGHTNING_ROD("Lightning Rod", Material.BLAZE_ROD, 12, ConfigEntry.SHOP_PRICES_LIGHTNING_ROD, ChatColor.LIGHT_PURPLE, "lightningRod", "/lightningrod"),
    FIRE_BALL("Fire Ball", Material.FIRE_CHARGE, 14, ConfigEntry.SHOP_PRICES_FIRE_BALL, ChatColor.RED, "fireBall", "/fireball"),
    RIDEABLE_PEARL("Rideable Ender Pearl", Material.ENDER_PEARL, 16, ConfigEntry.SHOP_PRICES_RIDEABLE_PEARL, ChatColor.DARK_PURPLE, "rideablePearl", "/rideablepearl"),
    STACKING_POTATO("Stacking Potato", Material.POTATO, 19, ConfigEntry.SHOP_PRICES_STACKING_POTATO, ChatColor.YELLOW, "stackingPotato", "/stackingpotato"),
    CLOWN_FISH("Clown Fish", Material.TROPICAL_FISH, 21, ConfigEntry.SHOP_PRICES_CLOWN_FISH, ChatColor.GOLD, "clownFish", "/clownfish"),
    LOGIN_MESSAGES("Login Messages", Material.NAME_TAG, 23, ConfigEntry.SHOP_PRICES_LOGIN_MESSAGES, ChatColor.DARK_GREEN, "loginMessages", "/loginmessage"),
    RAINBOW_TRAIL("Rainbow Trail", Material.RED_WOOL, 25, ConfigEntry.SHOP_PRICES_RAINBOW_TRAIL, ChatColor.DARK_RED, "rainbowTrail", "/trail");

    /*
        Shop GUI Layout:

        Dimensions: 9x4 = 36
        Key:
        g = Grappling Hook,
        l = Lightning Rod
        f = Fire Ball
        r = Rideable Ender Pearl
        s = Stacking Potato
        c = Clown Fish
        x = Login Messages
        t = Rainbow Trail
        $ = Coins

        ---------
        -g-l-f-r-
        -s-c-x-t-
        --------$
    */


    private final String name;

    private final Material icon;

    private final int slot;
    private final ConfigEntry cost;

    private final ChatColor color;

    private final String dataName;

    private final String command;

    ShopItem(String name, Material icon, int slot, ConfigEntry cost, ChatColor color, String dataName, String command)
    {
        this.name = name;
        this.icon = icon;
        this.slot = slot;
        this.cost = cost;
        this.color = color;
        this.dataName = dataName;
        this.command = command;
    }

    public static ShopItem findItem(String string)
    {
        try
        {
            return ShopItem.valueOf(string.toUpperCase());
        }
        catch (Exception ignored)
        {
        }

        return null;
    }

    public String getColoredName()
    {
        return color + name;
    }

    public int getCost()
    {
        return cost.getInteger();
    }

    public String getName()
    {
        return name;
    }

    public Material getIcon()
    {
        return icon;
    }

    public int getSlot()
    {
        return slot;
    }

    public ChatColor getColor()
    {
        return color;
    }

    public String getDataName()
    {
        return dataName;
    }

    public String getCommand()
    {
        return command;
    }
}