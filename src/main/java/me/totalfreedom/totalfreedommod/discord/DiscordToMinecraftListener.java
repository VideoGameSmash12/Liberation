package me.totalfreedom.totalfreedommod.discord;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.rank.Title;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DiscordToMinecraftListener extends ListenerAdapter
{
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String chat_channel_id = ConfigEntry.DISCORD_CHAT_CHANNEL_ID.getString();
        if (event.getMember() != null && !chat_channel_id.isEmpty() && event.getChannel().getId().equals(chat_channel_id))
        {
            if (!event.getAuthor().getId().equals(Discord.bot.getSelfUser().getId()))
            {
                Member member = event.getMember();
                String tag = getDisplay(member);
                Message msg = event.getMessage();

                ComponentBuilder emsg = new ComponentBuilder();

                // Prefix
                emsg.append(ChatColor.DARK_GRAY + "[");
                TextComponent inviteLink = new TextComponent("Discord");
                inviteLink.setColor(ChatColor.DARK_AQUA.asBungee());
                inviteLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new Text("Click here to get the invite link!")));
                inviteLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        ConfigEntry.DISCORD_INVITE_LINK.getString()));
                emsg.append(inviteLink);
                emsg.append(ChatColor.DARK_GRAY + "] ", ComponentBuilder.FormatRetention.NONE);

                // Tag (if they have one)
                if (tag != null)
                {
                    emsg.append(tag);
                }

                emsg.append(" ");

                // User
                TextComponent user = new TextComponent(ChatColor.stripColor(member.getEffectiveName()));
                user.setColor(ChatColor.RED.asBungee());
                emsg.append(user);

                // Message
                emsg.append(ChatColor.DARK_GRAY + ": " + ChatColor.RESET
                        + ChatColor.stripColor(msg.getContentDisplay()), ComponentBuilder.FormatRetention.NONE);

                // Attachments
                if (!msg.getAttachments().isEmpty())
                {
                    if (!msg.getContentDisplay().isEmpty())
                        emsg.append(" ");

                    for (Message.Attachment attachment : msg.getAttachments())
                    {
                        TextComponent media = new TextComponent("[Media] ");
                        media.setColor(ChatColor.YELLOW.asBungee());
                        media.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl()));
                        media.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(attachment.getUrl())));

                        emsg.append(media, ComponentBuilder.FormatRetention.NONE);
                    }
                }

                BaseComponent[] components = emsg.create();

                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (TotalFreedomMod.getPlugin().pl.getData(player).doesDisplayDiscord())
                    {
                        player.spigot().sendMessage(components);
                    }
                }

                FLog.info(TextComponent.toLegacyText(components), true);
            }
        }
    }

    public String getDisplay(Member member)
    {
        Guild server = Discord.bot.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());
        // Server Owner
        assert server != null;
        if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_SERVER_OWNER_ROLE_ID.getString())))
        {
            return Title.OWNER.getColoredTag();
        }
        // Developers
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_DEVELOPER_ROLE_ID.getString())))
        {
            return Title.DEVELOPER.getColoredTag();
        }
        // Executives
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_EXECUTIVE_ROLE_ID.getString())))
        {
            return Title.EXECUTIVE.getColoredTag();
        }
        // Assistant Executives
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_ASSISTANT_EXECUTIVE_ROLE_ID.getString())))
        {
            return Title.ASSTEXEC.getColoredTag();
        }
        // Senior Admins
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_SENIOR_ADMIN_ROLE_ID.getString())))
        {
            return Rank.SENIOR_ADMIN.getColoredTag();
        }
        // Admins
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_NEW_ADMIN_ROLE_ID.getString())))
        {
            return Rank.ADMIN.getColoredTag();
        }
        // Master Builders
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_MASTER_BUILDER_ROLE_ID.getString())))
        {
            return Title.MASTER_BUILDER.getColoredTag();
        }
        // None
        else
        {
            return null;
        }
    }
}