package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

public class SetTextAlignCommand extends AbstractEditDisplayEntityCommand {
    public SetTextAlignCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "[align]";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1) {
            return false;
        }
        TextDisplay.TextAlignment align;
        try {
            align = TextDisplay.TextAlignment.valueOf(args.getNext().toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("Invalid value for align").color(NamedTextColor.RED));
            return true;
        }

        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der Position des Display-Entitys entfernt!").color(NamedTextColor.RED));
            return true;
        }
        ((TextDisplay) displayEntity.getEntity()).setAlignment(align);

        String name = getNameAndOwner(player, displayEntity);
        player.sendMessage(Component.text("Das Display-Entity " + name + "hat nun das Alignment " + StringUtil.capitalizeFirstLetter(align.name(), true) + ".").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            return Arrays.asList(TextDisplay.TextAlignment.values()).stream().map(s -> s.name().toLowerCase()).toList();
        }
        return List.of();
    }
}
