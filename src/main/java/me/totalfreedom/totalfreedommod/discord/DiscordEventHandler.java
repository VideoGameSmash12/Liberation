package me.totalfreedom.totalfreedommod.discord;

import com.google.common.base.Strings;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.rank.Title;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.videogamesm12.liberation.event.AdminChatEvent;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DiscordEventHandler extends ListenerAdapter
{
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        final String chat_channel_id = ConfigEntry.DISCORD_CHAT_CHANNEL_ID.getString();
        final MessageChannel chatChannel = event.getChannel();

        if (event.getMember() == null
                || chat_channel_id.isEmpty()
                || event.getAuthor().getId().equals(Discord.BOT.getSelfUser().getId())
                || !(chatChannel instanceof final TextChannel textChannel))
        {
            return;
        }

        final Member member = event.getMember();
        final String tag = getDisplay(member);
        final Message msg = event.getMessage();
        final String content = msg.getContentStripped();

        if (content.startsWith(ConfigEntry.DISCORD_PREFIX.getString()))
        {
            Discord.DISCORD_COMMAND_MANAGER.parse(content, member, textChannel);
            return;
        }

        if (chatChannel.getId().equals(ConfigEntry.DISCORD_ADMINCHAT_CHANNEL_ID.getString()))
        {
            List<String> attachments = new ArrayList<>();

            msg.getAttachments().forEach(attachment ->
                    attachments.add(attachment.getUrl()));

            new AdminChatEvent(member.getUser().getAsTag(), Title.DISCORD, msg.getContentDisplay(), attachments, true).callEvent();
        }
        else if (chatChannel.getId().equals(ConfigEntry.DISCORD_CHAT_CHANNEL_ID.getString()))
        {
            // Prefix
            // TODO: Migrate this to an event and use MiniMessage for formatting instead
            TextComponent.Builder builder = Component.text();
            builder.append(
                    Component.text("[", TextColor.color(0x555555)),
                    Component.text("Discord", TextColor.color(0x5555FF))
                            .clickEvent(ClickEvent.openUrl(ConfigEntry.DISCORD_INVITE_LINK.getString()))
                            .hoverEvent(HoverEvent.showText(Component.text("Click here to open the invite link!"))),
                    Component.text("] ", TextColor.color(0x555555))
            );

            // Tag (if present)
            builder.append(LegacyComponentSerializer.legacyAmpersand().deserializeOr(tag + " ", Component.text("")));

            // User
            User user = member.getUser();
            builder.append(
                    Component.text(FUtil.stripColors(member.getEffectiveName())).color(TextColor.color(0xFF5555))
                            .hoverEvent(HoverEvent.showText(Component.text(user.getAsTag() + " (" + user.getId() + ")")))
            );

            // Message
            builder.append(
                    Component.text(" » ", TextColor.color(0x555555)),
                    Component.text(FUtil.stripColors(msg.getContentStripped())),
                    Component.text(msg.getContentDisplay().isEmpty() ? "" : " ")
            );

            // Attachments
            msg.getAttachments().forEach(attachment -> builder.append(
                    Component.text("[Media] ", TextColor.color(0xFFFF55))
                            .hoverEvent(HoverEvent.showText(Component.text(attachment.getUrl())))
                            .clickEvent(ClickEvent.openUrl(attachment.getUrl()))
            ));

            // Sends the message
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> TotalFreedomMod.getPlugin().pl.getData(player).doesDisplayDiscord())
                    .forEach(player -> player.sendMessage(builder.asComponent()));

            FLog.info(LegacyComponentSerializer.legacySection().serialize(builder.asComponent()), true);
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {
        if (!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())
                && event.getMessage().getContentRaw().matches("[0-9][0-9][0-9][0-9][0-9]"))
        {
            String code = event.getMessage().getContentRaw();
            String name;

            if (Discord.LINK_CODES.get(code) != null)
            {
                PlayerData player = Discord.LINK_CODES.get(code);
                name = player.getName();
                player.setDiscordID(event.getMessage().getAuthor().getId());

                Admin admin = TotalFreedomMod.getPlugin().al.getEntryByUuid(player.getUuid());
                if (admin != null)
                {
                    Discord.syncRoles(admin, player.getDiscordID());
                }

                TotalFreedomMod.getPlugin().pl.save(player);
                Discord.LINK_CODES.remove(code);
            }
            else
            {
                return;
            }
            event.getChannel().sendMessage("Link successful. Now this Discord account is linked with your Minecraft account **" + name + "**.").complete();
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event)
    {
        final TextChannel archiveChannel = Discord.BOT.getTextChannelById(ConfigEntry.DISCORD_REPORT_ARCHIVE_CHANNEL_ID.getString());
        final Message message = event.retrieveMessage().complete();
        final Member completer = event.getMember();

        if (!event.isFromGuild()
                || completer == null
                || completer.getUser().getId().equals(Discord.BOT.getSelfUser().getId())
                || !event.getChannel().getId().equals(ConfigEntry.DISCORD_REPORT_CHANNEL_ID.getString())
                || !event.getReactionEmote().getEmoji().equals("\uD83D\uDCCB")
                || !Strings.isNullOrEmpty(ConfigEntry.DISCORD_REPORT_ARCHIVE_CHANNEL_ID.getString())
                || archiveChannel == null
                || !message.getAuthor().getId().equals(Discord.BOT.getSelfUser().getId()))
        {
            return;
        }

        // Get the existing embed from the message we grabbed earlier
        final MessageEmbed embed = message.getEmbeds().get(0);

        // Build a new message using the existing one declaring the report as completed and sends it
        final MessageBuilder archiveMessageBuilder = new MessageBuilder();
        archiveMessageBuilder.setContent("Handled by " + completer.getUser().getAsMention());
        archiveMessageBuilder.setEmbeds(embed);
        final Message archiveMessage = archiveMessageBuilder.build();
        archiveChannel.sendMessage(archiveMessage).complete();

        // Deletes the original message
        message.delete().complete();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        TotalFreedomMod.getPlugin().dc.messageChatChannel("**Server has started**", true);
    }

    public String getDisplay(Member member)
    {
        Guild server = Discord.BOT.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());
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