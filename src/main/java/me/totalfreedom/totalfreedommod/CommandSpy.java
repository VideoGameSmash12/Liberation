package me.totalfreedom.totalfreedommod;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandSpy extends FreedomService
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        Player from = event.getPlayer();

        TextComponent.Builder component = Component.text();
        component.append(
                Component.text(from.getName())
                        .clickEvent(ClickEvent.copyToClipboard(from.getName()))
                        .hoverEvent(HoverEvent.showText(Component.translatable("chat.copy.click"))),
                Component.text(": "),
                Component.text(event.getMessage())
        );
        component.color(TextColor.color(0xAAAAAA));

        server.getOnlinePlayers().stream().filter(player -> plugin.al.isAdmin(player) && plugin.al.getAdmin(player).getCommandSpy() && player != from).forEach(player -> player.sendMessage(component));
    }
}