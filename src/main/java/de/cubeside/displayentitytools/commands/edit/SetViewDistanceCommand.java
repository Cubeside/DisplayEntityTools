package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

public class SetViewDistanceCommand extends AbstractEditDisplayEntityCommand {
    public SetViewDistanceCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean hasRequiredType(DisplayEntityType type) {
        return type == DisplayEntityType.BLOCK || type == DisplayEntityType.ITEM || type == DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "[blocks]";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1) {
            return false;
        }
        float dist;
        {
            String str = args.getNext();
            try {
                dist = str.isEmpty() ? 0 : Float.parseFloat(str);
            } catch (NumberFormatException e) {
                Messages.sendError(player, Component.text("Invalid value for view distance: " + str));
                return true;
            }
        }
        Display display = (Display) (displayEntity.getEntity());
        display.setViewRange(dist / 32);

        Component name = displayEntity.getNameAndOwner(player);
        String fdist = format.format(dist);
        Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("hat nun die Sichtweite " + fdist + "  Blöcke (bei 100% Entity-Renderdistanz).")));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (displayEntity.getEntity() instanceof Display display) {
            if (args.remaining() == 1) {
                return List.of(format.format(display.getViewRange() * 32));
            }
        }
        return List.of();
    }
}
