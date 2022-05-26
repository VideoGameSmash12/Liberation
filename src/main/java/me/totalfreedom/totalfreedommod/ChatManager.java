package me.totalfreedom.totalfreedommod;

import com.google.common.base.Strings;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FSync;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import static me.totalfreedom.totalfreedommod.util.FUtil.playerMsg;

public class ChatManager extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChatFormat(AsyncPlayerChatEvent event)
    {
        try
        {
            handleChatEvent(event);
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    private void handleChatEvent(AsyncPlayerChatEvent event)
    {
        final Player player = event.getPlayer();
        String message = event.getMessage().trim();

        // Format colors and strip &k
        message = FUtil.colorize(message);
        message = message.replaceAll(ChatColor.MAGIC.toString(), "&k");

        if (ConfigEntry.SHOP_ENABLED.getBoolean() && ConfigEntry.SHOP_REACTIONS_ENABLED.getBoolean() && !plugin.sh.reactionString.isEmpty() && message.equals(plugin.sh.reactionString))
        {
            event.setCancelled(true);
            PlayerData data = plugin.pl.getData(player);
            data.setCoins(data.getCoins() + plugin.sh.coinsPerReactionWin);
            plugin.pl.save(data);
            plugin.sh.endReaction(player.getName());
            player.sendMessage(ChatColor.GREEN + "You have been given " + ChatColor.GOLD + plugin.sh.coinsPerReactionWin + ChatColor.GREEN + " coins!");
            return;
        }

        if (!ConfigEntry.TOGGLE_CHAT.getBoolean() && !plugin.al.isAdmin(player))
        {
            event.setCancelled(true);
            playerMsg(player, "Chat is currently disabled.", org.bukkit.ChatColor.RED);
            return;
        }

        // Truncate messages that are too long - 256 characters is vanilla client max
        if (message.length() > 256)
        {
            message = message.substring(0, 256);
            FSync.playerMsg(player, "Message was shortened because it was too long to send.");
        }

        final FPlayer fPlayer = plugin.pl.getPlayerSync(player);
        if (fPlayer.isLockedUp())
        {
            FSync.playerMsg(player, "You're locked up and cannot talk.");
            event.setCancelled(true);
            return;
        }

        // Check for adminchat
        if (fPlayer.inAdminChat())
        {
            FSync.adminChatMessage(player, message);
            event.setCancelled(true);
            return;
        }

        // Check for 4chan trigger
        boolean green = ChatColor.stripColor(message).toLowerCase().startsWith(">");
        boolean orange = ChatColor.stripColor(message).toLowerCase().endsWith("<");
        if (ConfigEntry.FOURCHAN_ENABLED.getBoolean())
        {
            if (green)
            {
                message = ChatColor.GREEN + message;
            }
            else if (orange)
            {
                message = ChatColor.GOLD + message;
            }
        }

        // Finally, set message
        event.setMessage(message);

        // Make format
        String format = "%1$s §8\u00BB §f%2$s";

        String tag = fPlayer.getTag();
        if (tag != null && !tag.isEmpty())
        {
            format = tag.replace("%", "%%") + " " + format;
        }

        // Check for mentions
        boolean mentionEveryone = ChatColor.stripColor(message).toLowerCase().contains("@everyone") && plugin.al.isAdmin(player);
        for (Player p : server.getOnlinePlayers())
        {
            if (ChatColor.stripColor(message).toLowerCase().contains("@" + p.getName().toLowerCase()) || mentionEveryone)
            {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1337F, 0.9F);
            }
        }

        // Set format
        event.setFormat(format);

        // Send to discord
        if (!ConfigEntry.ADMIN_ONLY_MODE.getBoolean() && !Bukkit.hasWhitelist() && !plugin.pl.getPlayer(player).isMuted() && !plugin.tfg.inGuildChat(player))
        {
            plugin.dc.messageChatChannel(player.getName() + " \u00BB " + ChatColor.stripColor(message));
        }
    }

    public ChatColor getColor(Displayable display)
    {
        return display.getColor();
    }

    public String getColoredTag(Displayable display)
    {
        ChatColor color = display.getColor();
        return color + display.getAbbr();
    }

    public void adminChat(CommandSender sender, String message)
    {
        Displayable display = plugin.rm.getDisplay(sender);
        FLog.info("[ADMIN] " + sender.getName() + " " + display.getTag() + ": " + message, true);
        plugin.dc.messageAdminChatChannel(sender.getName() + " \u00BB " + message);

        server.getOnlinePlayers().stream().filter(player -> plugin.al.isAdmin(player)).forEach(player ->
        {
            Admin admin = plugin.al.getAdmin(player);
            if (!Strings.isNullOrEmpty(admin.getAcFormat())) {
                String format = admin.getAcFormat();
                ChatColor color = getColor(display);
                String msg = format.replace("%name%", sender.getName()).replace("%rank%", display.getAbbr()).replace("%rankcolor%", color.toString()).replace("%msg%", message);
                player.sendMessage(FUtil.colorize(msg));
            }
            else
            {
                player.sendMessage("[" + ChatColor.AQUA + "ADMIN" + ChatColor.WHITE + "] " + ChatColor.DARK_RED + sender.getName() + ChatColor.DARK_GRAY + " [" + getColoredTag(display) + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + ChatColor.GOLD + FUtil.colorize(message));
            }
        });
    }

    public void reportAction(Player reporter, String reportedName, String report)
    {
        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player))
            {
                playerMsg(player, ChatColor.RED + "[REPORTS] " + ChatColor.GOLD + reporter.getName() + " has reported " + reportedName + " for " + report);
            }
        }
        FLog.info("[REPORTS] " + reporter.getName() + " has reported " + reportedName + " for " + report);
    }
}