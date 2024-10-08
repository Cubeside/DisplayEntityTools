package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;

public class SetBillboardModeCommand extends AbstractEditDisplayEntityCommand {
    public SetBillboardModeCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return null;
    }

    @Override
    public String getUsage() {
        return "[mode]";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1) {
            return false;
        }
        Billboard billboard;
        try {
            billboard = Billboard.valueOf(args.getNext().toUpperCase());
        } catch (IllegalArgumentException e) {
            Messages.sendError(player, "Invalid value for billboard mode");
            return true;
        }

        displayEntity.getEntity().setBillboard(billboard);

        Component name = displayEntity.getNameAndOwner(player);
        Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("hat nun den Billboard-Modus " + StringUtil.capitalizeFirstLetter(billboard.name(), true) + ".")));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            return Arrays.asList(Billboard.values()).stream().map(s -> s.name().toLowerCase()).toList();
        }
        return List.of();
    }
}
