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

public class InfoCommand extends AbstractEditDisplayEntityCommand {
    public InfoCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return null;
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() > 0) {
            return false;
        }

        if (displayEntity.getOwner().size() == 0) {
            Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(displayEntity.getColoredName()).append(Component.text("hat keinen Besitzer.")));
        } else {
            Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(displayEntity.getColoredName()).append(Component.text("hat " + (displayEntity.getOwner().size() == 1 ? "den" : "die") + " Besitzer: ")).append(displayEntity.getColoredOwners()));
        }

        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        return List.of();
    }
}
