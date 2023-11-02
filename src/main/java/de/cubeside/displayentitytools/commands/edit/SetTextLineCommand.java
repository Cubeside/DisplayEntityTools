package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.StringUtilCore;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

public class SetTextLineCommand extends AbstractEditDisplayEntityCommand {
    public enum Mode {
        SET,
        INSERT
    }

    private Mode mode;

    public SetTextLineCommand(DisplayEntityToolsPlugin plugin, Mode mode) {
        super(plugin);
        this.mode = mode;
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "<line> <text>";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() < 1) {
            return false;
        }
        int lineNumber = args.getNext(-1) - 1;
        String text = args.getAll("");
        String addstr = StringUtilCore.convertColors(text);

        Component textComp = ((TextDisplay) displayEntity.getEntity()).text();
        String oldStr = LegacyComponentSerializer.legacySection().serialize(textComp);
        ArrayList<String> lines = new ArrayList<>(List.of(oldStr.split("\n")));

        int lineMax = Math.max(lines.size() + 1, 10);
        if (lineNumber < 0 || lineNumber >= lineMax) {
            player.sendMessage(Component.text("Ungültige Zeilennummer! (1 bis " + lineMax + ")").color(NamedTextColor.RED));
            return true;
        }
        while (lines.size() < lineNumber + (mode == Mode.SET ? 1 : 0)) {
            lines.add("");
        }
        if (mode == Mode.SET) {
            lines.set(lineNumber, addstr);
        } else {
            lines.add(lineNumber, addstr);
        }

        StringBuilder newStr = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                newStr.append("\n");
            }
            newStr.append(lines.get(i));
        }

        Component textComponent = LegacyComponentSerializer.legacySection().deserialize(newStr.toString());

        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der Position des Display-Entities entfernt!").color(NamedTextColor.RED));
            return true;
        }
        ((TextDisplay) displayEntity.getEntity()).text(textComponent);

        String name = getNameAndOwner(player, displayEntity);
        player.sendMessage(Component.text("Text für das Display-Entity " + name + "wurde bearbeitet.").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            Component textComp = ((TextDisplay) displayEntity.getEntity()).text();
            String oldStr = LegacyComponentSerializer.legacySection().serialize(textComp);
            int oldLength = oldStr.split("\n").length;
            int lineMax = Math.max(oldLength + 1, 10) + 1;
            ArrayList<String> result = new ArrayList<>();
            for (int i = 1; i < lineMax; i++) {
                result.add(Integer.toString(i));
            }
            return result;
        }
        if (mode == Mode.SET && args.remaining() == 2) {
            int line = args.getNext(-1) - 1;
            Component textComp = ((TextDisplay) displayEntity.getEntity()).text();
            String oldStr = LegacyComponentSerializer.legacySection().serialize(textComp);
            String[] split = oldStr.split("\n");
            if (line >= 0 && line < split.length) {
                return List.of(StringUtilCore.revertColors(split[line]));
            }
            return List.of();
        }
        return List.of();
    }
}
