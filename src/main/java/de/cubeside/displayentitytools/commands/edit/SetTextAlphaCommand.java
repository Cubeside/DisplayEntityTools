package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

public class SetTextAlphaCommand extends AbstractEditDisplayEntityCommand {
    public SetTextAlphaCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "<alpha>";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1) {
            return false;
        }
        String alphaString = args.getNext();
        int alpha = -1;
        try {
            alpha = Integer.parseInt(alphaString);
        } catch (NumberFormatException e) {
        }
        if (alpha < 0 || alpha > 255) {
            player.sendMessage(Component.text("Ungültiger Alpha-Wert (0...255): " + alphaString).color(NamedTextColor.RED));
            return true;
        }

        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der Position des Display-Entitys entfernt!").color(NamedTextColor.RED));
            return true;
        }
        ((TextDisplay) displayEntity.getEntity()).setTextOpacity((byte) alpha);

        String name = displayEntity.getName() == null ? "" : "'" + displayEntity.getName() + "'";
        player.sendMessage(Component.text("Der Text des Display-Entitys " + name + " hat nun eine ALpha-Transparenz von " + alpha + ".").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            int alpha = ((TextDisplay) displayEntity.getEntity()).getTextOpacity() & 0xff;
            return List.of(Integer.toString(alpha));
        }
        return List.of();
    }
}
