package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.StringUtilCore;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

public class SetTextCommand extends AbstractEditDisplayEntityCommand {
    public enum Mode {
        SET,
        ADD,
        ADDLINE
    }

    private Mode mode;

    public SetTextCommand(DisplayEntityToolsPlugin plugin, Mode mode) {
        super(plugin);
        this.mode = mode;
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "<text>";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() < 1) {
            return false;
        }
        String text = args.getAll("");
        String str = StringUtilCore.convertColors(text);
        if (mode == Mode.ADD || mode == Mode.ADDLINE) {
            Component textComp = ((TextDisplay) displayEntity.getEntity()).text();
            String oldStr = textComp == null ? null : LegacyComponentSerializer.legacySection().serialize(textComp);
            if (oldStr != null && !oldStr.isBlank()) {
                if (mode == Mode.ADDLINE) {
                    str = oldStr + "\n" + str;
                } else {
                    str = oldStr + str;
                }
            }
        }
        Component textComponent = LegacyComponentSerializer.legacySection().deserialize(str);

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
        if (mode == Mode.SET && args.remaining() == 1) {
            Component textComp = ((TextDisplay) displayEntity.getEntity()).text();

            String text = textComp == null ? null : StringUtilCore.revertColors(LegacyComponentSerializer.legacySection().serialize(textComp));
            return text == null ? List.of() : List.of(text);
        }
        return List.of();
    }
}
