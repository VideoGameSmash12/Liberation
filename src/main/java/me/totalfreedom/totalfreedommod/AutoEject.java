package me.totalfreedom.totalfreedommod;

import java.util.*;

import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class AutoEject extends FreedomService
{
    private final Map<UUID, Integer> ejects = new HashMap<>(); // uuid -> amount

    public void autoEject(Player player, String kickMessage)
    {
        EjectMethod method = EjectMethod.STRIKE_ONE;

        if (!ejects.containsKey(player.getUniqueId()))
        {
            ejects.put(player.getUniqueId(), 0);
        }

        int kicks = ejects.get(player.getUniqueId());
        kicks += 1;

        ejects.put(player.getUniqueId(), kicks);

        if (kicks == 2)
        {
            method = EjectMethod.STRIKE_TWO;
        }
        else if (kicks >= 3)
        {
            method = EjectMethod.STRIKE_THREE;
        }

        FLog.info("AutoEject -> name: " + player.getName() + " - player ip: " + player.getUniqueId() + " - method: " + method.name());

        player.setOp(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();

        switch (method)
        {
            case STRIKE_ONE:
            {
                final Calendar cal = new GregorianCalendar();
                cal.add(Calendar.MINUTE, 5);
                final Date expires = cal.getTime();

                FUtil.bcastMsg(ChatColor.RED + player.getName() + " has been banned for 5 minutes.");

                plugin.bm.addBan(Ban.forPlayer(player, Bukkit.getConsoleSender(), expires, kickMessage));
                player.kickPlayer(kickMessage);

                break;
            }
            case STRIKE_TWO:
            {
                final Calendar c = new GregorianCalendar();
                c.add(Calendar.MINUTE, 10);
                final Date expires = c.getTime();

                FUtil.bcastMsg(ChatColor.RED + player.getName() + " has been banned for 10 minutes.");

                plugin.bm.addBan(Ban.forPlayer(player, Bukkit.getConsoleSender(), expires, kickMessage));
                player.kickPlayer(kickMessage);
                break;
            }
            default:
            {
                FLog.warning("Unrecognized EjectMethod " + method.name() + " found, defaulting to STRIKE_THREE");
            }
            case STRIKE_THREE:
            {
                plugin.bm.addBan(Ban.forPlayerFuzzy(player, Bukkit.getConsoleSender(), null, kickMessage));

                FUtil.bcastMsg(ChatColor.RED + player.getName() + " has been banned.");

                player.kickPlayer(kickMessage);
                break;
            }
        }
    }

    public enum EjectMethod
    {
        STRIKE_ONE, STRIKE_TWO, STRIKE_THREE
    }

}
