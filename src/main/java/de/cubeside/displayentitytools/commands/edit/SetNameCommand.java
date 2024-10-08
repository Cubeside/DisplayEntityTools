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

public class SetNameCommand extends AbstractEditDisplayEntityCommand {
    public SetNameCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return null;
    }

    @Override
    public String getUsage() {
        return "[name]";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        Component oldName = displayEntity.getNameAndOwner(player);
        String name = args.hasNext() ? args.getAll("") : null;
        displayEntity.setName(name);

        if (name == null) {
            Messages.sendSuccess(player, Component.text("Der Name des Display-Entities ").append(oldName).append(Component.text("wurde entfernt!")));
        } else {
            Messages.sendSuccess(player, Component.text("Der Name des Display-Entities ").append(oldName).append(Component.text("wurde auf '")).append(displayEntity.getColoredName()).append(Component.text("' gesetzt!")));
        }
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            String name = displayEntity.getName();
            return name == null ? List.of() : List.of(name);
        }
        return List.of();
    }
}
