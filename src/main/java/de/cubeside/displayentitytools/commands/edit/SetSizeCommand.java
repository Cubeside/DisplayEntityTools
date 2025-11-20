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
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

public class SetSizeCommand extends AbstractEditDisplayEntityCommand {
    public SetSizeCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean hasRequiredType(DisplayEntityType type) {
        return type == DisplayEntityType.INTERACTION;
    }

    @Override
    public String getUsage() {
        return "[width] [height]";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 2) {
            return false;
        }
        float width = (float) args.getNext(0.0);
        float height = (float) args.getNext(0.0);
        if (!Float.isFinite(width) || width <= 0 || width > 5) {
            Messages.sendError(player, "Width must be positive and less than 5");
            return true;
        }
        if (!Float.isFinite(height) || height <= 0 || height > 5) {
            Messages.sendError(player, "Height must be positive and less than 5");
            return true;
        }

        Interaction interaction = (Interaction) displayEntity.getEntity();
        interaction.setInteractionWidth(width);
        interaction.setInteractionHeight(height);

        Component name = displayEntity.getNameAndOwner(player);
        Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("hat nun die Breite " + format.format(width) + " und die Höhe " + format.format(height) + ".")));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (displayEntity.getEntity() instanceof Interaction interaction) {
            if (args.remaining() == 1) {
                return List.of(format.format(interaction.getInteractionWidth()));
            } else if (args.remaining() == 2) {
                return List.of(format.format(interaction.getInteractionHeight()));
            }
        }
        return List.of();
    }
}
