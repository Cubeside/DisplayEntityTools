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

public class SetShadowCommand extends AbstractEditDisplayEntityCommand {
    public SetShadowCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return null;
    }

    @Override
    public String getUsage() {
        return "[radius] [strength]";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 2) {
            return false;
        }
        float shadowRadius = Float.NaN;
        float shadowStrength = Float.NaN;
        {
            String str = args.getNext();
            try {
                shadowRadius = Float.parseFloat(str);
            } catch (NumberFormatException e) {
            }
            if (!Float.isFinite(shadowRadius) || shadowRadius < 0 || shadowRadius > 3) {
                player.sendMessage(Component.text("Invalid value for shadow radius (0..3): " + str).color(NamedTextColor.RED));
                return true;
            }
        }
        {
            String str = args.getNext();
            try {
                shadowStrength = Float.parseFloat(str);
            } catch (NumberFormatException e) {
            }
            if (!Float.isFinite(shadowStrength)) {
                player.sendMessage(Component.text("Invalid value for shadow strength: " + str).color(NamedTextColor.RED));
                return true;
            }
        }

        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der Position des Display-Entitys entfernt!").color(NamedTextColor.RED));
            return true;
        }
        displayEntity.getEntity().setShadowRadius(shadowRadius);
        displayEntity.getEntity().setShadowStrength(shadowStrength);

        String name = displayEntity.getName() == null ? "" : "'" + displayEntity.getName() + "'";
        player.sendMessage(Component.text("Das Display-Entity " + name + " hat nun den Schattenradius " + format.format(shadowRadius) + " mit Stärke " + format.format(shadowStrength) + ".").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            return List.of(format.format(displayEntity.getEntity().getShadowRadius()));
        } else if (args.remaining() == 2) {
            return List.of(format.format(displayEntity.getEntity().getShadowStrength()));
        }
        return List.of();
    }
}
