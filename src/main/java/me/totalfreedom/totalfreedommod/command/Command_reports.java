package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.videogamesm12.liberation.services.ReportSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@CommandParameters(description = "Returns a list of reports.", usage = "/<command> <list [<number>] | details <id>>")
@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
public class Command_reports extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        switch (args[0].toLowerCase())
        {
            case "list" ->
            {
                List<ReportSystem.Report> reports = plugin.rs.getReports().values().stream().filter(report -> isAdmin(sender) || report.getReporter().equals(playerSender.getUniqueId())).sorted((a, b) -> (int) (a.getTimestamp() - b.getTimestamp())).toList();
                int page = args.length > 1 ? Integer.parseInt(args[2]) : 1;

                // Pagination
                FUtil.PaginationList<ReportSystem.Report> reportList = new FUtil.PaginationList<>(5, reports.toArray(new ReportSystem.Report[0]));
                if (reportList.isEmpty())
                {
                    plugin.rs.notify(sender, ReportSystem.NotificationType.LOOKUP, Component.text("The reports database is currently empty."));
                    return true;
                }
                else if (page <= 0 || reportList.getPageCount() < page)
                {
                    sender.sendMessage(Component.text("Invalid page.").color(TextColor.color(0xFF5555)));
                    return true;
                }

                TextComponent.Builder header = Component.text();

                header.append(
                        Component.text("==", TextColor.color(0x0000FF)),
                        Component.text(" Search Results ", TextColor.color(0xFFFFFF)),
                        Component.text("("),
                        Component.text(page + "/" + reportList.getPageCount(), TextColor.color(0xFFFFFF)),
                        Component.text(") "),
                        Component.text("==", TextColor.color(0x0000FF))
                );

                plugin.rs.notify(sender, ReportSystem.NotificationType.LOOKUP, header.build());
                for (ReportSystem.Report report : reportList.getPage(page))
                    plugin.rs.notify(sender, ReportSystem.NotificationType.LOOKUP, report.asNotification());

            }
            case "details" ->
            {
                if (args.length == 1)
                {
                    plugin.rs.notify(sender, ReportSystem.NotificationType.NEUTRAL,
                            Component.text("Usage: ").append(Component.text("/reports details <id>",
                                    TextColor.color(0xFFFFFF))));
                    return true;
                }

                String id = args[1];

                if (!plugin.rs.getReports().containsKey(id))
                {
                    plugin.rs.notify(sender, ReportSystem.NotificationType.BAD, Component.text("No reports matching that ID were found."));
                }
                else
                {
                    plugin.rs.getReports().get(id).detailsToComponent().forEach(component ->
                            plugin.rs.notify(sender, ReportSystem.NotificationType.LOOKUP, component));
                }

                return true;
            }
        }

        return true;
    }

    @Override
    protected List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length < 2)
        {
            return List.of("list", "details");
        }

        return Collections.emptyList();
    }
}
