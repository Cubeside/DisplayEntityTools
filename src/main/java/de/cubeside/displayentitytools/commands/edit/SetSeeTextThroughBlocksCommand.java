package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

public class SetSeeTextThroughBlocksCommand extends AbstractEditDisplayEntityCommand {
    public SetSeeTextThroughBlocksCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "true|false";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1) {
            return false;
        }
        Boolean seeThrough = args.getNext(false);
        if (seeThrough == null) {
            return false;
        }

        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der Position des Display-Entities entfernt!").color(NamedTextColor.RED));
            return true;
        }
        ((TextDisplay) displayEntity.getEntity()).setSeeThrough(seeThrough);

        String name = getNameAndOwner(player, displayEntity);
        player.sendMessage(Component.text("Das Display-Entity " + name + "wird nun " + (seeThrough ? "" : "nicht ") + "durch Blöcke hindurch gesehen.").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            return List.of("true", "false");
        }
        return List.of();
    }
}
