package me.videogamesm12.liberation.event;

import me.totalfreedom.totalfreedommod.rank.Displayable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <h1>AdminChatEvent</h1>
 * <p>Called when an admin sends a message to the admin chat.</p>
 */
public class AdminChatEvent extends Event
{
    private static HandlerList handlerList = new HandlerList();
    //--
    private final String name;
    private final Displayable displayable;
    private final String message;
    private List<String> attachments;

    public AdminChatEvent(String name, Displayable displayable, String message, List<String> attachments)
    {
        this(name, displayable, message, attachments, false);
    }

    public AdminChatEvent(String name, Displayable displayable, String message, List<String> attachments, boolean async)
    {
        super(async);
        this.name = name;
        this.displayable = displayable;
        this.message = message;
        this.attachments = attachments;
    }

    public String getName()
    {
        return name;
    }

    public Displayable getDisplayable()
    {
        return displayable;
    }

    public String getMessage()
    {
        return message;
    }

    public List<String> getAttachments()
    {
        return attachments;
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers()
    {
        return handlerList;
    }
}
