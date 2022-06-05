package me.totalfreedom.totalfreedommod.discord;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReactionListener extends ListenerAdapter
{
    public void onMessageReactionAdd(MessageReactionAddEvent messageReactionAddEvent)
    {
        if (!messageReactionAddEvent.isFromGuild())
        {
            return;
        }

        if (messageReactionAddEvent.getMember() == null)
        {
            return;
        }

        if (messageReactionAddEvent.getMember().getUser().getId().equals(Discord.bot.getSelfUser().getId()))
        {
            return;
        }

        if (!messageReactionAddEvent.getChannel().getId().equals(ConfigEntry.DISCORD_REPORT_CHANNEL_ID.getString()))
        {
            return;
        }

        if (!messageReactionAddEvent.getReactionEmote().getEmoji().equals("\uD83D\uDCCB"))
        {
            return;
        }

        final TextChannel archiveChannel = Discord.bot.getTextChannelById(ConfigEntry.DISCORD_REPORT_ARCHIVE_CHANNEL_ID.getString());

        if (archiveChannel == null)
        {
            FLog.warning("Report archive channel is defined in the config, yet doesn't actually exist!");
            return;
        }

        final Message message = messageReactionAddEvent.retrieveMessage().complete();
        final Member completer = messageReactionAddEvent.getMember();

        if (!message.getAuthor().getId().equals(Discord.bot.getSelfUser().getId()))
        {
            return;
        }

        // We don't need other embeds... yet?
        final MessageEmbed embed = message.getEmbeds().get(0);
        final MessageBuilder archiveMessageBuilder = new MessageBuilder();
        archiveMessageBuilder.setContent("Report completed by " + completer.getUser().getAsMention() + " (" + Discord.deformat(completer.getUser().getAsTag() + ")"));
        archiveMessageBuilder.setEmbed(embed);
        final Message archiveMessage = archiveMessageBuilder.build();

        archiveChannel.sendMessage(archiveMessage).complete();
        message.delete().complete();
    }
}
