package me.totalfreedom.totalfreedommod.discord;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrivateMessageListener extends ListenerAdapter
{
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {
        if (!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
        {
            // Handle link code
            if (event.getMessage().getContentRaw().matches("[0-9][0-9][0-9][0-9][0-9]"))
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
    }
}