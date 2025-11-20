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

public class SetShadowCommand extends AbstractEditDisplayEntityCommand {
    public SetShadowCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean hasRequiredType(DisplayEntityType type) {
        return type == DisplayEntityType.BLOCK || type == DisplayEntityType.ITEM || type == DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "[radius] [strength]";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 2) {
            return false;
        }
        float shadowRadius = Float.NaN;
        float shadowStrength = Float.NaN;
        {
            String str = args.getNext();
            try {
                shadowRadius = Float.parseFloat(str);
            } catch (NumberFormatException e) {
            }
            if (!Float.isFinite(shadowRadius) || shadowRadius < 0 || shadowRadius > 3) {
                Messages.sendError(player, "Invalid value for shadow radius (0..3): " + str);
                return true;
            }
        }
        {
            String str = args.getNext();
            try {
                shadowStrength = Float.parseFloat(str);
            } catch (NumberFormatException e) {
            }
            if (!Float.isFinite(shadowStrength)) {
                Messages.sendError(player, "Invalid value for shadow strength: " + str);
                return true;
            }
        }

        ((Display) displayEntity.getEntity()).setShadowRadius(shadowRadius);
        ((Display) displayEntity.getEntity()).setShadowStrength(shadowStrength);

        Component name = displayEntity.getNameAndOwner(player);
        Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("hat nun den Schattenradius " + format.format(shadowRadius) + " mit Stärke " + format.format(shadowStrength) + ".")));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            return List.of(format.format(((Display) displayEntity.getEntity()).getShadowRadius()));
        } else if (args.remaining() == 2) {
            return List.of(format.format(((Display) displayEntity.getEntity()).getShadowStrength()));
        }
        return List.of();
    }
}
