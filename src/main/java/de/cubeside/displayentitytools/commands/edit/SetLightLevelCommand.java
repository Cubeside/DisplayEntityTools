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
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Player;

public class SetLightLevelCommand extends AbstractEditDisplayEntityCommand {
    public SetLightLevelCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean hasRequiredType(DisplayEntityType type) {
        return type == DisplayEntityType.BLOCK || type == DisplayEntityType.ITEM || type == DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "[blocklight] [sunlight]";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() < 1 || args.remaining() > 2) {
            return false;
        }
        boolean lightAuto = false;
        int blocklight = Integer.MIN_VALUE;
        int sunlight = Integer.MIN_VALUE;
        {
            String str = args.getNext();
            if (str.equalsIgnoreCase("auto")) {
                lightAuto = true;
            } else {
                try {
                    blocklight = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                }
                if (blocklight < 0 || blocklight > 15) {
                    Messages.sendError(player, "Invalid value for block light (0..15): " + str);
                    return true;
                }
            }
        }
        if (args.hasNext()) {
            if (lightAuto) {
                Messages.sendError(player, "Automatic light level is always for both components");
                return true;
            }
            String str = args.getNext();
            try {
                sunlight = Integer.parseInt(str);
            } catch (NumberFormatException e) {
            }
            if (sunlight < 0 || sunlight > 15) {
                Messages.sendError(player, "Invalid value for sun light (0..15): " + str);
                return true;
            }
        } else {
            sunlight = blocklight;
        }

        ((Display) displayEntity.getEntity()).setBrightness(lightAuto ? null : new Brightness(blocklight, sunlight));

        Component name = displayEntity.getNameAndOwner(player);
        if (lightAuto) {
            Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("hat nun automatische Lichtlevel.")));
        } else {
            Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("hat nun die Lichtlevel Blocklicht " + blocklight + " und Sonnenlight " + sunlight + ".")));
        }
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            return List.of("auto", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15");
        } else if (args.remaining() == 2) {
            if (args.getNext().equalsIgnoreCase("auto")) {
                return List.of();
            } else {
                return List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15");
            }
        }
        return List.of();
    }
}
