package de.cubeside.displayentitytools.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

public class Messages {
    private Messages() {
    }

    public static Component getPrefix() {
        return Component.text("[", TextColor.fromCSSHexString("#4090ff")).append(Component.text("DE", TextColor.fromCSSHexString("#80c0ff"))).append(Component.text("]"));
    }

    public static void send(CommandSender sender, Component text, TextColor defaultColor) {
        if (defaultColor != null) {
            TextColor setColor = text.color();
            if (setColor == null) {
                text = text.color(defaultColor);
            }
        }
        text = Component.text("").append(getPrefix()).append(Component.text(" ")).append(text);
        sender.sendMessage(text);
    }

    public static void sendSuccess(CommandSender sender, Component text) {
        send(sender, text, NamedTextColor.GREEN);
    }

    public static void sendSuccess(CommandSender sender, String text) {
        sendSuccess(sender, Component.text(text));
    }

    public static void sendError(CommandSender sender, Component text) {
        send(sender, text, NamedTextColor.DARK_RED);
    }

    public static void sendError(CommandSender sender, String text) {
        sendError(sender, Component.text(text));
    }
}
