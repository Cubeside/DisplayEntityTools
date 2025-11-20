package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SetRotationCommand extends AbstractEditDisplayEntityCommand {
    public SetRotationCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean hasRequiredType(DisplayEntityType type) {
        return type == DisplayEntityType.BLOCK || type == DisplayEntityType.ITEM || type == DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "[alpha] [pitch]";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1 && args.remaining() != 2) {
            return false;
        }
        Location displayLoc = displayEntity.getLocation();
        double alpha;
        {
            String str = args.getNext();
            boolean rel = false;
            if (str.startsWith("~")) {
                rel = true;
                str = str.substring(1);
            }
            try {
                alpha = str.isEmpty() ? 0 : Double.parseDouble(str);
            } catch (NumberFormatException e) {
                Messages.sendError(player, Component.text("Invalid value for alpha: " + str));
                return true;
            }
            if (rel) {
                alpha += displayLoc.getYaw();
            }
        }

        double pitch = Double.NaN;
        if (args.hasNext()) {
            String str = args.getNext();
            boolean rel = false;
            if (str.startsWith("~")) {
                rel = true;
                str = str.substring(1);
            }
            try {
                pitch = str.isEmpty() ? 0 : Double.parseDouble(str);
            } catch (NumberFormatException e) {
                Messages.sendError(player, "Invalid value for pitch: " + str);
                return true;
            }
            if (rel) {
                pitch += displayLoc.getPitch();
            }
            pitch = Math.min(pitch, 90);
            pitch = Math.max(pitch, -90);
        }

        Location newDisplayLoc = displayLoc.clone();
        newDisplayLoc.setYaw((float) alpha);
        if (!Double.isNaN(pitch)) {
            newDisplayLoc.setPitch((float) pitch);
        }
        displayEntity.teleport(newDisplayLoc);

        Component name = displayEntity.getNameAndOwner(player);
        String falpha = format.format(alpha);
        String fpitch = Double.isNaN(pitch) ? "" : format.format(pitch);
        Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("hat nun die Rotation " + falpha + (Double.isNaN(pitch) ? "" : (" und den Pitch " + fpitch)) + ".")));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            return List.of("~", format.format(displayEntity.getLocation().getYaw()));
        }
        if (args.remaining() == 2) {
            return List.of("~", format.format(displayEntity.getLocation().getPitch()));
        }
        return List.of();
    }
}
