package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                player.sendMessage(Component.text("Invalid value for x: " + str).color(NamedTextColor.RED));
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
                player.sendMessage(Component.text("Invalid value for y: " + str).color(NamedTextColor.RED));
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
                player.sendMessage(Component.text("Invalid value for z: " + str).color(NamedTextColor.RED));
                return true;
            }
            if (rel) {
                z += displayLoc.getZ();
            }
        }
        Location newDisplayLoc = displayLoc.clone().set(x, y, z);
        if (newDisplayLoc.distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der neuen Position des Display-Entitys entfernt!").color(NamedTextColor.RED));
            return true;
        }
        if (plugin.getWorldGuardHelper() != null && !plugin.getWorldGuardHelper().canBuild(player, newDisplayLoc)) {
            player.sendMessage(Component.text("Du hast an der Zielposition keine Baurechte.").color(NamedTextColor.RED));
            return true;
        }

        displayEntity.teleport(newDisplayLoc);

        String name = getNameAndOwner(player, displayEntity);
        String fx = format.format(x);
        String fy = format.format(y);
        String fz = format.format(z);
        player.sendMessage(Component.text("Das Display-Entity " + name + "wurde an die Position " + fx + " " + fy + " " + fz + " bewegt.").color(NamedTextColor.GREEN));
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
