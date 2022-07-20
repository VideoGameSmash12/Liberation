package me.totalfreedom.totalfreedommod.discord;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.rank.Title;
import me.videogamesm12.liberation.event.AdminChatEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiscordToAdminChatListener extends ListenerAdapter
{
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String chat_channel_id = ConfigEntry.DISCORD_ADMINCHAT_CHANNEL_ID.getString();
        if (event.getMember() != null && !chat_channel_id.isEmpty() && event.getChannel().getId().equals(chat_channel_id) && !event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
        {
            Member member = event.getMember();
            Message msg = event.getMessage();
            List<String> attachments = new ArrayList<>();

            msg.getAttachments().forEach(attachment ->
            {
                attachments.add(attachment.getUrl());
            });

            new AdminChatEvent(member.getUser().getName() + "#" + member.getUser().getDiscriminator(), Title.DISCORD, msg.getContentDisplay(), attachments, true).callEvent();
        }
    }

    // Needed to display tags in custom AC messages
    public Displayable getDisplay(Member member)
    {
        Guild server = Discord.bot.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());
        // Server Owner
        if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_SERVER_OWNER_ROLE_ID.getString())))
        {
            return Title.OWNER;
        }
        // Developers
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_DEVELOPER_ROLE_ID.getString())))
        {
            return Title.DEVELOPER;
        }
        // Executives
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_EXECUTIVE_ROLE_ID.getString())))
        {
            return Title.EXECUTIVE;
        }
        // Assistant Executives
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_ASSISTANT_EXECUTIVE_ROLE_ID.getString())))
        {
            return Title.ASSTEXEC;
        }
        // Senior Admins
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_SENIOR_ADMIN_ROLE_ID.getString())))
        {
            return Rank.SENIOR_ADMIN;
        }
        // Admins
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_NEW_ADMIN_ROLE_ID.getString())))
        {
            return Rank.ADMIN;
        }
        // Master Builders
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_MASTER_BUILDER_ROLE_ID.getString())))
        {
            return Title.MASTER_BUILDER;
        }
        // OP, returning null breaks?
        else
        {
            return Rank.OP;
        }
    }
}
