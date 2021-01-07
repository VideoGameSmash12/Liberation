package me.totalfreedom.totalfreedommod.blocking.command;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.admin.Admin;
import org.bukkit.command.CommandSender;

public enum CommandBlockerRank
{
    EVERYONE("e"),
    OP("o"),
    ADMIN("a"),
    SENIOR_ADMIN("s"),
    NOBODY("n");
    //
    private final String token;

    CommandBlockerRank(String token)
    {
        this.token = token;
    }

    public String getToken()
    {
        return this.token;
    }

    public boolean hasPermission(CommandSender sender)
    {
        return fromSender(sender).ordinal() >= ordinal();
    }

    public static CommandBlockerRank fromSender(CommandSender sender)
    {
        Admin admin = TotalFreedomMod.getPlugin().al.getAdmin(sender);
        if (admin != null)
        {
            if (admin.getRank() == Rank.SENIOR_ADMIN)
            {
                return SENIOR_ADMIN;
            }
            return ADMIN;
        }

        if (sender.isOp())
        {
            return OP;
        }

        return EVERYONE;
    }

    public static CommandBlockerRank fromToken(String token)
    {
        for (CommandBlockerRank rank : CommandBlockerRank.values())
        {
            if (rank.getToken().equalsIgnoreCase(token))
            {
                return rank;
            }
        }
        return EVERYONE;
    }
}