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
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4d;
import org.joml.Quaternionf;

public class SetTransformRotationCommand extends AbstractEditDisplayEntityCommand {
    private boolean left;

    public SetTransformRotationCommand(DisplayEntityToolsPlugin plugin, boolean left) {
        super(plugin);
        this.left = left;
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return null;
    }

    @Override
    public String getUsage() {
        return "<axis_x> <axis_y> <axis_z> <alpha>";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 4) {
            return false;
        }
        double x, y, z, alpha;
        {
            String str = args.getNext();
            try {
                x = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                Messages.sendError(player, "Invalid value for x: " + str);
                return true;
            }
        }
        {
            String str = args.getNext();
            try {
                y = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                Messages.sendError(player, "Invalid value for y: " + str);
                return true;
            }
        }
        {
            String str = args.getNext();
            try {
                z = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                Messages.sendError(player, "Invalid value for z: " + str);
                return true;
            }
        }
        {
            String str = args.getNext();
            try {
                alpha = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                Messages.sendError(player, "Invalid value for alpha: " + str);
                return true;
            }
        }
        double lengthSq = x * x + y * y + z * z;
        if (lengthSq > 0) {
            double length = Math.sqrt(lengthSq);
            x /= length;
            y /= length;
            z /= length;
        } else {
            x = 0;
            y = 1;
            z = 0;
        }

        Transformation transform = displayEntity.getEntity().getTransformation();
        Quaternionf newRotation = new Quaternionf(new AxisAngle4d(alpha * Math.PI / 180.0, x, y, z));
        Transformation newTransform;
        if (left) {
            newTransform = new Transformation(transform.getTranslation(), newRotation, transform.getScale(), transform.getRightRotation());
        } else {
            newTransform = new Transformation(transform.getTranslation(), transform.getLeftRotation(), transform.getScale(), newRotation);
        }
        displayEntity.getEntity().setTransformation(newTransform);

        Component name = displayEntity.getNameAndOwner(player);
        Messages.sendSuccess(player, Component.text("Die " + (left ? "linke" : "rechte") + " Rotation der Transformation vom Display-Entity ").append(name).append(Component.text("wurde gesetzt.")));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        Quaternionf quart = left ? displayEntity.getEntity().getTransformation().getLeftRotation() : displayEntity.getEntity().getTransformation().getRightRotation();
        AxisAngle4d axisAngle = new AxisAngle4d(quart);
        if (args.remaining() == 1) {
            return List.of(format.format(axisAngle.x));
        } else if (args.remaining() == 2) {
            return List.of(format.format(axisAngle.y));
        } else if (args.remaining() == 3) {
            return List.of(format.format(axisAngle.z));
        } else if (args.remaining() == 4) {
            return List.of(format.format(axisAngle.angle * 180 / Math.PI));
        }
        return List.of();
    }
}
