package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

public class DeleteTextLineCommand extends AbstractEditDisplayEntityCommand {
    public DeleteTextLineCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "<line>";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1) {
            return false;
        }
        int lineNumber = args.getNext(-1) - 1;

        Component textComp = ((TextDisplay) displayEntity.getEntity()).text();
        String oldStr = LegacyComponentSerializer.legacySection().serialize(textComp);
        ArrayList<String> lines = new ArrayList<>(List.of(oldStr.split("\n", -1)));

        int lineMax = lines.size();
        if (lineNumber < 0 || lineNumber >= lineMax) {
            Messages.sendError(player, "Ungültige Zeilennummer! (1 bis " + lineMax + ")");
            return true;
        }
        lines.remove(lineNumber);

        StringBuilder newStr = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                newStr.append("\n");
            }
            newStr.append(lines.get(i));
        }

        Component textComponent = LegacyComponentSerializer.legacySection().deserialize(newStr.toString());

        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            Messages.sendError(player, "Du bist zu weit von der Position des Display-Entities entfernt!");
            return true;
        }
        ((TextDisplay) displayEntity.getEntity()).text(textComponent);

        Component name = displayEntity.getNameAndOwner(player);
        Messages.sendSuccess(player, Component.text("Text für das Display-Entity ").append(name).append(Component.text("wurde bearbeitet.")));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            Component textComp = ((TextDisplay) displayEntity.getEntity()).text();
            String oldStr = LegacyComponentSerializer.legacySection().serialize(textComp);
            int oldLength = oldStr.split("\n", -1).length;
            ArrayList<String> result = new ArrayList<>();
            for (int i = 1; i <= oldLength; i++) {
                result.add(Integer.toString(i));
            }
            return result;
        }
        return List.of();
    }
}
