package me.totalfreedom.totalfreedommod.httpd.module;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.command.FreedomCommand;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginIdentifiableCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.totalfreedom.totalfreedommod.httpd.HTMLGenerationTools.heading;
import static me.totalfreedom.totalfreedommod.httpd.HTMLGenerationTools.paragraph;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class Module_help extends HTTPDModule
{
    public Module_help(NanoHTTPD.HTTPSession session)
    {
        super(session);
    }

    private static String buildDescription(@NotNull Command command)
    {
        return buildDescription(command.getName(), command.getDescription(), command.getUsage(), StringUtils.join(command.getAliases(), ", "));
    }

    private static String buildDescription(@NotNull FreedomCommand command)
    {
        return buildDescription(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
    }

    private static String buildDescription(@NotNull String name, @Nullable String description, @NotNull String usage, @NotNull String aliases)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(
                "<li><span class=\"commandName\">{$CMD_NAME}</span> - Usage: <span class=\"commandUsage\">{$CMD_USAGE}</span>"
                        .replace("{$CMD_NAME}", escapeHtml4(name.trim()))
                        .replace("{$CMD_USAGE}", escapeHtml4(usage.trim())));

        if (!aliases.isEmpty())
        {
            sb.append(
                    " - Aliases: <span class=\"commandAliases\">{$CMD_ALIASES}</span>"
                            .replace("{$CMD_ALIASES}", escapeHtml4(aliases.trim())));
        }

        if (description != null)
        {
            sb.append(
                    "<br><span class=\"commandDescription\">{$CMD_DESC}</span></li>\r\n"
                            .replace("{$CMD_DESC}", escapeHtml4(description.trim())));
        }

        return sb.toString();
    }

    @Override
    public String getBody()
    {
        final CommandMap map = Bukkit.getCommandMap();

        final StringBuilder responseBody = new StringBuilder()
                .append(heading("Command Help", 1))
                .append(paragraph(
                        "This page is an automatically generated listing of all plugin commands that are currently live on the server. "
                                + "Please note that it does not include vanilla server commands."));

        final Collection<Command> knownCommands = map.getKnownCommands().values();
        final Map<String, List<Command>> commandsByPlugin = new HashMap<>();

        for (Command command : knownCommands)
        {
            String pluginName = "Bukkit";
            if (command instanceof PluginIdentifiableCommand)
            {
                pluginName = ((PluginIdentifiableCommand)command).getPlugin().getName();
            }

            List<Command> pluginCommands = commandsByPlugin.computeIfAbsent(pluginName, k -> Lists.newArrayList());

            if (!pluginCommands.contains(command))
            {
                pluginCommands.add(command);
            }
        }

        final CommandComparator comparator = new CommandComparator();

        // For every plugin...
        for (Map.Entry<String, List<Command>> entry : commandsByPlugin.entrySet())
        {
            final String pluginName = entry.getKey();
            final List<Command> commands = entry.getValue();

            // Sort them alphabetically
            commands.sort(comparator);

            responseBody.append(heading(pluginName, pluginName, 2)).append("<ul>\r\n");

            if (!plugin.getName().equals(pluginName))
            {
                commands.forEach((command) -> responseBody.append(buildDescription(command)));
            }
            else
            {
                Map<Rank, List<FreedomCommand>> freedomCommands = new HashMap<>();

                // Filters out non-TFM commands
                commands.stream().filter((cmd) -> cmd instanceof FreedomCommand.FCommand).forEach((tfmCmd) -> {
                    Rank rank = FreedomCommand.getFrom(tfmCmd).getLevel();
                    if (!freedomCommands.containsKey(rank))
                        freedomCommands.put(rank, new ArrayList<>());
                    freedomCommands.get(rank).add(FreedomCommand.getFrom(tfmCmd));
                });

                // Finally dumps them to HTML
                Arrays.stream(Rank.values()).filter(freedomCommands::containsKey)
                        .sorted(comparator::compare).forEach((rank -> {
                    responseBody.append("</ul>\r\n").append(heading(rank.getName(), 3)).append("<ul>\r\n");
                    freedomCommands.get(rank).stream().sorted(comparator::compare).forEach((command) -> responseBody.append(buildDescription(command)));
                }));
            }

            responseBody.append("</ul>\r\n");
        }

        return responseBody.toString();
    }

    @Override
    public String getTitle()
    {
        return plugin.getName() + " :: Command Help";
    }

    @Override
    public String getStyle()
    {
        return ".commandName{font-weight:bold;}.commandDescription{padding-left:15px;}li{margin:.15em;padding:.15em;}";
    }

    public static class CommandComparator implements Comparator<Command>
    {
        @Override
        public int compare(Command a, Command b)
        {
            return a.getName().compareTo(b.getName());
        }

        public int compare(FreedomCommand a, FreedomCommand b)
        {
            return a.getName().compareTo(b.getName());
        }

        public int compare(Rank a, Rank b)
        {
            Integer levelA = a.getLevel();
            Integer levelB = b.getLevel();

            return levelB.compareTo(levelA);
        }
    }
}
