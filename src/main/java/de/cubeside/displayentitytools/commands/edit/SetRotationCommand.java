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

public class SetRotationCommand extends AbstractEditDisplayEntityCommand {
    public SetRotationCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return null;
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
                player.sendMessage(Component.text("Invalid value for alpha: " + str).color(NamedTextColor.RED));
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
                player.sendMessage(Component.text("Invalid value for pitch: " + str).color(NamedTextColor.RED));
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
        if (newDisplayLoc.distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der Position des Display-Entities entfernt!").color(NamedTextColor.RED));
            return true;
        }
        displayEntity.teleport(newDisplayLoc);

        String name = getNameAndOwner(player, displayEntity);
        String falpha = format.format(alpha);
        String fpitch = Double.isNaN(pitch) ? "" : format.format(pitch);
        player.sendMessage(Component.text("Das Display-Entity " + name + "hat nun die Rotation " + falpha + (Double.isNaN(pitch) ? "" : (" und den Pitch " + fpitch)) + ".").color(NamedTextColor.GREEN));
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
