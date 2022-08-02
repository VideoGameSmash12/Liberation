package me.totalfreedom.totalfreedommod.command;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Check the name history of a specified player.", usage = "/<command> <username>", aliases = "nh")
public class Command_namehistory extends FreedomCommand
{
    private static final Gson gson = new Gson();
    private static final Pattern filter = Pattern.compile("^[A-z0-9_]{1,16}$");

    @Override
    public boolean run(final CommandSender sender, final Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (!filter.matcher(args[0]).matches())
        {
            sender.sendMessage(Component.text("Invalid username: " + args[0], TextColor.color(0xFF5555)));
            return true;
        }

        sender.sendMessage(Component.text("Connecting...", TextColor.color(0x55FF55)));
        server.getScheduler().runTaskAsynchronously(plugin, bukkitTask -> {
            try
            {
                URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + args[0]);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                AshconResponse response = gson.fromJson(new BufferedReader(new InputStreamReader(con.getInputStream())), AshconResponse.class);
                if (response.usernameHistory.size() == 1)
                {
                    sender.sendMessage(Component.text(response.username + " has never changed their username.", TextColor.color(0x55FF55)));
                }
                else
                {
                    for (AshconResponse.NameHistoryEntry entry : response.usernameHistory)
                        sender.sendMessage(entry.toComponent());
                }
            }
            catch (FileNotFoundException ex)
            {
                sender.sendMessage(Component.text("Player not found!", TextColor.color(0xFF5555)));
            }
            catch (Exception ex)
            {
                sender.sendMessage(Component.text("Error: " + ex.getMessage(), TextColor.color(0xFF5555)));
            }
        });

        return true;
    }

    public static class AshconResponse
    {
        private UUID uuid;
        private String username;
        @SerializedName("username_history")
        private List<NameHistoryEntry> usernameHistory = new ArrayList<>();

        public static class NameHistoryEntry implements Comparator<NameHistoryEntry>
        {
            // 2019-02-26T21:53:42.000Z
            private static SimpleDateFormat ashconFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH':'mm':'ss'.'SSS'Z'");
            private static SimpleDateFormat ourFormat = new SimpleDateFormat("yyyy-MM-dd HH':'mm':'ss");
            //--
            private String username;
            @SerializedName("changed_at")
            private String changedAt;

            private long getChangedAt()
            {
                if (changedAt == null)
                    return 0;

                try
                {
                    return ashconFormat.parse(changedAt).getTime();
                }
                catch (ParseException ex)
                {
                    return 0;
                }
            }

            public Component toComponent()
            {
                TextComponent.Builder builder = Component.text();

                if (changedAt == null)
                {
                    builder.append(Component.text("Original name: ", TextColor.color(0xFFAA00)));
                }
                else
                {
                    builder.append(Component.text(ourFormat.format(getChangedAt()), TextColor.color(0x5555FF)));
                    builder.append(Component.text(" changed to ", TextColor.color(0xFFAA00)));
                }

                builder.append(Component.text(username, TextColor.color(0x55FF55))
                        .clickEvent(ClickEvent.copyToClipboard(username))
                        .hoverEvent(HoverEvent.showText(Component.translatable("chat.copy.click"))));

                return builder.build();
            }

            @Override
            public int compare(NameHistoryEntry o1, NameHistoryEntry o2)
            {
                return Long.compare(o1.getChangedAt(), o2.getChangedAt());
            }
        }
    }
}