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
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SetTransformMatrixCommand extends AbstractEditDisplayEntityCommand {
    public SetTransformMatrixCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean hasRequiredType(DisplayEntityType type) {
        return type == DisplayEntityType.BLOCK || type == DisplayEntityType.ITEM || type == DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "<matrix4x4>";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1 && args.remaining() != 12 && args.remaining() != 16) {
            Messages.sendError(player, "Invalid number of arguments. The parameter must be a transform matrix!");
            return false;
        }
        Matrix4f matrix = new Matrix4f();
        String current = "";
        try {
            String[] parts;
            if (args.remaining() == 1) {
                parts = args.getNext().split(",");
            } else {
                parts = args.toArray();
            }
            if (parts.length != 12 && parts.length != 16) {
                Messages.sendError(player, "Invalid number of arguments. The parameter must be a transform matrix!");
                return false;
            }
            for (int i = 0; i < parts.length; ++i) {
                current = cleanup(parts[i]);
                float f = Float.parseFloat(current);
                if (!Float.isFinite(f)) {
                    throw new NumberFormatException(current + " is not finite.");
                }
                matrix.setRowColumn(i >> 2, i & 3, f);
            }
            matrix.determineProperties();
        } catch (NumberFormatException e) {
            Messages.sendError(player, "The matrix could not be parsed! The first invalid value is: " + current);
            return false;
        }
        if (!matrix.isAffine()) {
            Messages.sendError(player, "The transformation must be affine!");
            return false;
        }
        Display display = (Display) (displayEntity.getEntity());
        Transformation transform = display.getTransformation();
        display.setTransformationMatrix(matrix);
        Transformation newTransform = display.getTransformation();

        if (!player.hasPermission(DisplayEntityToolsPermissions.PERMISSION_UNLIMITED_VALUES)) {
            int limit = 20;
            Vector3f v = newTransform.getScale();
            if (v.x < -limit || v.x > limit || v.y < -limit || v.y > limit || v.z < -limit || v.z > limit) {
                Messages.sendError(player, "Die Skalierungs-Werte müssen zwischen -" + limit + " und " + limit + " liegen");
                display.setTransformation(transform);
                return true;
            }

            limit = 5;
            v = newTransform.getTranslation();
            if (v.x < -limit || v.x > limit || v.y < -limit || v.y > limit || v.z < -limit || v.z > limit) {
                Messages.sendError(player, "Die Translations-Werte müssen zwischen -" + limit + " und " + limit + " liegen");
                display.setTransformation(transform);
                return true;
            }
        }

        Component name = displayEntity.getNameAndOwner(player);
        Messages.sendSuccess(player, Component.text("Die Matrix-Transformation vom Display-Entity ").append(name).append(Component.text("wurde gesetzt.")));
        return true;
    }

    private String cleanup(String string) {
        if (string.endsWith("f")) {
            string = string.substring(0, string.length() - 1);
        } else if (string.endsWith("d")) {
            string = string.substring(0, string.length() - 1);
        }
        string = string.replace("[", "").replace("]", "").replace("{", "").replace("}", "");
        return string;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        return List.of();
    }
}
