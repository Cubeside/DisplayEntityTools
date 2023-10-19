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
            player.sendMessage(Component.text("Invalid value for billboard mode").color(NamedTextColor.RED));
            return true;
        }

        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der Position des Display-Entities entfernt!").color(NamedTextColor.RED));
            return true;
        }
        displayEntity.getEntity().setBillboard(billboard);

        String name = getNameAndOwner(player, displayEntity);
        player.sendMessage(Component.text("Das Display-Entity " + name + "hat nun den Billboard-Modus " + StringUtil.capitalizeFirstLetter(billboard.name(), true) + ".").color(NamedTextColor.GREEN));
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
