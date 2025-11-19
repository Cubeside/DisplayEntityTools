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
import org.bukkit.entity.Player;

public class SetViewDistanceCommand extends AbstractEditDisplayEntityCommand {
    public SetViewDistanceCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return null;
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

        displayEntity.getEntity().setViewRange(dist);

        Component name = displayEntity.getNameAndOwner(player);
        String fdist = format.format(dist);
        Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("hat nun die Sichtweite " + fdist + " Blöcke.")));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            return List.of(format.format(displayEntity.getEntity().getViewRange()));
        }
        return List.of();
    }
}
