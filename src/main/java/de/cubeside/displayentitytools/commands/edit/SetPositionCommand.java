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

public class SetPositionCommand extends AbstractEditDisplayEntityCommand {
    public SetPositionCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return null;
    }

    @Override
    public String getUsage() {
        return "[x] [y] [z]";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 3) {
            return false;
        }
        Location displayLoc = displayEntity.getLocation();
        double x, y, z;
        {
            String str = args.getNext();
            boolean rel = false;
            if (str.startsWith("~")) {
                rel = true;
                str = str.substring(1);
            }
            try {
                x = str.isEmpty() ? 0 : Double.parseDouble(str);
            } catch (NumberFormatException e) {
                Messages.sendError(player, "Invalid value for x: " + str);
                return true;
            }
            if (rel) {
                x += displayLoc.getX();
            }
        }
        {
            String str = args.getNext();
            boolean rel = false;
            if (str.startsWith("~")) {
                rel = true;
                str = str.substring(1);
            }
            try {
                y = str.isEmpty() ? 0 : Double.parseDouble(str);
            } catch (NumberFormatException e) {
                Messages.sendError(player, "Invalid value for y: " + str);
                return true;
            }
            if (rel) {
                y += displayLoc.getY();
            }
        }
        {
            String str = args.getNext();
            boolean rel = false;
            if (str.startsWith("~")) {
                rel = true;
                str = str.substring(1);
            }
            try {
                z = str.isEmpty() ? 0 : Double.parseDouble(str);
            } catch (NumberFormatException e) {
                Messages.sendError(player, "Invalid value for z: " + str);
                return true;
            }
            if (rel) {
                z += displayLoc.getZ();
            }
        }
        Location newDisplayLoc = displayLoc.clone().set(x, y, z);
        if (newDisplayLoc.distanceSquared(player.getLocation()) > 100 * 100) {
            Messages.sendError(player, "Du bist zu weit von der neuen Position des Display-Entities entfernt!");
            return true;
        }
        if (plugin.getWorldGuardHelper() != null && !plugin.getWorldGuardHelper().canBuild(player, newDisplayLoc)) {
            Messages.sendError(player, "Du hast an der Zielposition keine Baurechte.");
            return true;
        }

        displayEntity.teleport(newDisplayLoc);

        Component name = displayEntity.getNameAndOwner(player);
        String fx = format.format(x);
        String fy = format.format(y);
        String fz = format.format(z);
        Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("wurde an die Position " + fx + " " + fy + " " + fz + " bewegt.")));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            return List.of("~", format.format(displayEntity.getLocation().getX()));
        } else if (args.remaining() == 2) {
            return List.of("~", format.format(displayEntity.getLocation().getY()));
        } else if (args.remaining() == 3) {
            return List.of("~", format.format(displayEntity.getLocation().getZ()));
        }
        return List.of();
    }
}
