package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPermissions;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

public class SetTextLineWidthCommand extends AbstractEditDisplayEntityCommand {
    public SetTextLineWidthCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "<width>";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1) {
            return false;
        }
        String alphaString = args.getNext();
        int lineWidth = -1;
        try {
            lineWidth = Integer.parseInt(alphaString);
        } catch (NumberFormatException e) {
        }
        if (lineWidth < 1 || (lineWidth > 5000 && !player.hasPermission(DisplayEntityToolsPermissions.PERMISSION_UNLIMITED_VALUES))) {
            Messages.sendError(player, "Ungültiger Zeilenbreite-Wert (1...5000): " + alphaString);
            return true;
        }

        ((TextDisplay) displayEntity.getEntity()).setLineWidth(lineWidth);

        Component name = displayEntity.getNameAndOwner(player);
        Messages.sendSuccess(player, Component.text("Der Text des Display-Entities ").append(name).append(Component.text("hat nun eine Zeilenbreite von " + lineWidth + " Pixeln.")));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            int lineWidth = ((TextDisplay) displayEntity.getEntity()).getLineWidth();
            return List.of(Integer.toString(lineWidth));
        }
        return List.of();
    }
}
