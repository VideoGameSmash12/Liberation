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
import me.videogamesm12.liberation.event.AdminChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

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

    @EventHandler
    public void onAdminChat(AdminChatEvent event)
    {
        Displayable displayable = event.getDisplayable();
        String sender = event.getName();
        String message = event.getMessage();
        List<String> attachments = event.getAttachments();

        FLog.info(FUtil.colorize(ConfigEntry.DEFAULT_ADMINCHAT_FORMAT.getString()
                .replace("%name%", sender)
                .replace("%rankcolor%", getColor(displayable).toString())
                .replace("%rank%", displayable.getAbbr())
                .replace("%msg%", message)
        ), true);

        server.getOnlinePlayers().stream().filter(player -> plugin.al.isAdmin(player)).forEach(
                player -> {
                    Admin admin = plugin.al.getAdmin(player);
                    String format = Strings.isNullOrEmpty(admin.getAcFormat()) ? ConfigEntry.DEFAULT_ADMINCHAT_FORMAT.getString() : admin.getAcFormat();

                    Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(
                            format.replace("%name%", sender)
                            .replace("%rankcolor%", getColor(displayable).toString())
                            .replace("%rank%", displayable.getAbbr())
                            .replace("%msg%", message)
                    );

                    for (String attachment : attachments)
                    {
                        component = component.append(Component.text(" "));
                        //
                        Component attach = Component.text("[Media]", TextColor.color(254, 254, 63))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, attachment));
                        //
                        component = component.append(attach);
                    }

                    player.sendMessage(component);
                }
        );
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
        new AdminChatEvent(sender.getName(), plugin.rm.getDisplay(sender), message, new ArrayList<>()).callEvent();
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