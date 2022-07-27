package me.videogamesm12.liberation.services;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.videogamesm12.liberation.event.PlayerReportEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportSystem extends FreedomService
{
    public static final Component PREFIX = Component.text("✉ ", TextColor.color(0xFFFFFF));
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    //--
    @Getter
    private Map<String, Report> reports = new HashMap<>();
    //--
    static
    {
        // The seed for IDs are calculated based on the exact time in epoch milliseconds the ReportSystem class was statically initialized.
        RANDOM.setSeed(new Date().toInstant().toEpochMilli());
    }

    @EventHandler
    public void onPlayerReport(PlayerReportEvent event)
    {
        server.getOnlinePlayers().stream().filter(player -> plugin.al.isAdmin(player)).forEach(admin -> {
            notify(admin, NotificationType.BAD, event.getReport().asNotification());
        });
    }

    public void notify(CommandSender sender, NotificationType type, Component component)
    {
        sender.sendMessage(PREFIX.append(Component.text("| ", TextColor.color(type.getColor()))).append(component.colorIfAbsent(TextColor.color(0xAAAAAA))));
    }

    public void fileReport(Player reporter, OfflinePlayer reported, String reason)
    {
        Report report = new Report(reporter, reported, reason);
        reports.put(report.id, report);
        new PlayerReportEvent(report).callEvent();

        notify(reporter, NotificationType.GOOD, Component.text("Thank you. Your report (")
                .append(Component.text("#" + report.id).clickEvent(ClickEvent.copyToClipboard("#" + report.id)).color(TextColor.color(0xFFFFFF)))
                .append(Component.text(") has been successfully logged.")));
    }

    public static String generateIdentifier()
    {
        byte[] bytes = new byte[6];
        RANDOM.nextBytes(bytes);
        //--
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public enum NotificationType
    {
        LOOKUP(0x5555FF),
        GOOD(0x55FF55),
        NEUTRAL(0xFFFF55),
        BAD(0xFF5555);

        @Getter
        private final int color;

        NotificationType(int color)
        {
            this.color = color;
        }
    }

    @Getter
    @Setter
    public static class Report
    {
        private final String id;
        private final UUID reporter;
        private final UUID reported;
        private long timestamp;
        private boolean reportedOnlineAtTime;
        private String reason;
        private boolean handled;

        /**
         * Constructor for generating new report instances.
         * @param reporter  Player
         * @param reported  OfflinePlayer
         * @param reason    String
         */
        public Report(Player reporter, OfflinePlayer reported, String reason)
        {
            this.id = generateIdentifier();
            this.reporter = reporter.getUniqueId();
            this.reported = reported.getUniqueId();
            this.timestamp = new Date().toInstant().toEpochMilli();
            this.reportedOnlineAtTime = reported.isOnline();
            this.reason = reason;
        }

        public Report(String id, UUID reporter, UUID reported, boolean reportedOnlineAtTime, String reason, long timestamp, boolean handled)
        {
            this.id = id;
            this.reporter = reporter;
            this.reported = reported;
            this.reportedOnlineAtTime = reportedOnlineAtTime;
            this.reason = reason;
            this.timestamp = timestamp;
            this.handled = handled;
        }

        public Component asNotification()
        {
            TextComponent.Builder builder = Component.text();
            //--
            OfflinePlayer reporterPlayer = Bukkit.getOfflinePlayer(reporter);
            OfflinePlayer reportedPlayer = Bukkit.getOfflinePlayer(reported);
            //--
            builder.append(
                    Component.text(Objects.requireNonNull(reporterPlayer.getName()), TextColor.color(0xFFFFFF)),
                    Component.text(" has reported "),
                    Component.text(Objects.requireNonNull(reportedPlayer.getName()), TextColor.color(0xFFFFFF)),
                    Component.text(" for "),
                    Component.text(reason != null ? reason : "No reason specified", TextColor.color(0xFFFFFF)),
                    Component.text(" ("),
                    Component.text("#" + id)
                            .clickEvent(ClickEvent.copyToClipboard("#" + id))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to copy to clipboard")))
                            .color(TextColor.color(0xFFFFFF)),
                    Component.text(")")
            );

            return builder.build();
        }

        public List<Component> detailsToComponent()
        {
            OfflinePlayer reporterPlayer = Bukkit.getOfflinePlayer(reporter);
            OfflinePlayer reportedPlayer = Bukkit.getOfflinePlayer(reported);
            //--
            return List.of(
                    Component.text("Report for " + reportedPlayer.getName(), TextColor.color(0x5555FF)).decorate(TextDecoration.BOLD),
                    Component.text().append(Component.text("Date: ", TextColor.color(0xFFFFFF)), Component.text(DATE_FORMAT.format(new Date(getTimestamp())))).build(),
                    Component.text().append(Component.text("Filed by: ", TextColor.color(0xFFFFFF)), Component.text(Objects.requireNonNull(reporterPlayer.getName()))).build(),
                    Component.text().append(Component.text("Reason: ", TextColor.color(0xFFFFFF)), Component.text(Strings.isNullOrEmpty(getReason()) ? "No reason specified" : getReason())).build(),
                    Component.text("--", TextColor.color(0x555555)),
                    Component.text().append(Component.text("Report ID: ", TextColor.color(0xFFFFFF)), Component.text("#" + id).clickEvent(ClickEvent.copyToClipboard(id)).hoverEvent(HoverEvent.showText(Component.text("Click to copy to clipboard")))).build(),
                    Component.text().append(Component.text("Status: ", TextColor.color(0xFFFFFF)).append(
                            handled ? Component.text("Handled").color(TextColor.color(0x55FF55))
                                    : Component.text("Not handled").color(TextColor.color(0xFF5555)))).build()
            );
        }
    }
}
