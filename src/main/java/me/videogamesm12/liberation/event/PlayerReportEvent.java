package me.videogamesm12.liberation.event;

import lombok.Getter;
import lombok.Setter;
import me.videogamesm12.liberation.services.ReportSystem;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class PlayerReportEvent extends Event
{
    @Getter
    private static HandlerList handlerList = new HandlerList();
    private ReportSystem.Report report;

    public PlayerReportEvent(ReportSystem.Report report)
    {
        this.report = report;
    }

    @Override
    public @NotNull HandlerList getHandlers()
    {
        return handlerList;
    }
}
