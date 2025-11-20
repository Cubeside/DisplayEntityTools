package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPermissions;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.entity.Display;
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
    public boolean hasRequiredType(DisplayEntityType type) {
        return type == DisplayEntityType.BLOCK || type == DisplayEntityType.ITEM || type == DisplayEntityType.TEXT;
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
        if (!player.hasPermission(DisplayEntityToolsPermissions.PERMISSION_UNLIMITED_VALUES)) {
            int sizeLimit = scale ? 20 : 5;
            if (x < -sizeLimit || x > sizeLimit) {
                Messages.sendError(player, "Die Werte müssen zwischen -" + sizeLimit + " und " + sizeLimit + " liegen");
                return true;
            }
            if (y < -sizeLimit || y > sizeLimit) {
                Messages.sendError(player, "Die Werte müssen zwischen -" + sizeLimit + " und " + sizeLimit + " liegen");
                return true;
            }
            if (z < -sizeLimit || z > sizeLimit) {
                Messages.sendError(player, "Die Werte müssen zwischen -" + sizeLimit + " und " + sizeLimit + " liegen");
                return true;
            }
        }
        Display display = (Display) (displayEntity.getEntity());
        Transformation transform = display.getTransformation();
        Vector3f newTranslationScale = new Vector3f((float) x, (float) y, (float) z);
        Transformation newTransform;
        if (scale) {
            newTransform = new Transformation(transform.getTranslation(), transform.getLeftRotation(), newTranslationScale, transform.getRightRotation());
        } else {
            newTransform = new Transformation(newTranslationScale, transform.getLeftRotation(), transform.getScale(), transform.getRightRotation());
        }
        display.setTransformation(newTransform);

        Component name = displayEntity.getNameAndOwner(player);
        Messages.sendSuccess(player, Component.text("Die " + (scale ? "Skalierung" : "Translation") + " der Transformation vom Display-Entity ").append(name).append(Component.text("wurde gesetzt.")));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (displayEntity.getEntity() instanceof Display display) {
            Vector3f v = scale ? display.getTransformation().getScale() : display.getTransformation().getTranslation();
            if (args.remaining() == 1) {
                return List.of(format.format(v.x));
            } else if (args.remaining() == 2) {
                return List.of(format.format(v.y));
            } else if (args.remaining() == 3) {
                return List.of(format.format(v.z));
            }
        }
        return List.of();
    }
}
