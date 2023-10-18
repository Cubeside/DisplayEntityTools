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
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class SetTransformTranslationScaleCommand extends AbstractEditDisplayEntityCommand {
    private boolean scale;

    public SetTransformTranslationScaleCommand(DisplayEntityToolsPlugin plugin, boolean scale) {
        super(plugin);
        this.scale = scale;
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return null;
    }

    @Override
    public String getUsage() {
        return "<x> <y> <z>";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 3) {
            return false;
        }
        double x, y, z;
        {
            String str = args.getNext();
            try {
                x = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("Invalid value for x: " + str).color(NamedTextColor.RED));
                return true;
            }
        }
        {
            String str = args.getNext();
            try {
                y = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("Invalid value for y: " + str).color(NamedTextColor.RED));
                return true;
            }
        }
        {
            String str = args.getNext();
            try {
                z = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("Invalid value for z: " + str).color(NamedTextColor.RED));
                return true;
            }
        }
        if (x < -5 || x > 5) {
            player.sendMessage(Component.text("Die Werte müssen zwischen -5 und 5 liegen").color(NamedTextColor.RED));
            return true;
        }
        if (y < -5 || y > 5) {
            player.sendMessage(Component.text("Die Werte müssen zwischen -5 und 5 liegen").color(NamedTextColor.RED));
            return true;
        }
        if (z < -5 || z > 5) {
            player.sendMessage(Component.text("Die Werte müssen zwischen -5 und 5 liegen").color(NamedTextColor.RED));
            return true;
        }

        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der Position des Display-Entitys entfernt!").color(NamedTextColor.RED));
            return true;
        }

        Transformation transform = displayEntity.getEntity().getTransformation();
        Vector3f newTranslationScale = new Vector3f((float) x, (float) y, (float) z);
        Transformation newTransform;
        if (scale) {
            newTransform = new Transformation(transform.getTranslation(), transform.getLeftRotation(), newTranslationScale, transform.getRightRotation());
        } else {
            newTransform = new Transformation(newTranslationScale, transform.getLeftRotation(), transform.getScale(), transform.getRightRotation());
        }
        displayEntity.getEntity().setTransformation(newTransform);

        String name = displayEntity.getName() == null ? "" : "'" + displayEntity.getName() + "'";
        player.sendMessage(Component.text("Die " + (scale ? "Skalierung" : "Translation") + " der Transformation vom Display-Entity " + name + " wurde gesetzt.").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        Vector3f v = scale ? displayEntity.getEntity().getTransformation().getScale() : displayEntity.getEntity().getTransformation().getTranslation();
        if (args.remaining() == 1) {
            return List.of(format.format(v.x));
        } else if (args.remaining() == 2) {
            return List.of(format.format(v.y));
        } else if (args.remaining() == 3) {
            return List.of(format.format(v.z));
        }
        return List.of();
    }
}
