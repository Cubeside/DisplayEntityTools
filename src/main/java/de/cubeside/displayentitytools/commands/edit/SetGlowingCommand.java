package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

public class SetGlowingCommand extends AbstractEditDisplayEntityCommand {
    public SetGlowingCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean hasRequiredType(DisplayEntityType type) {
        return type == DisplayEntityType.BLOCK || type == DisplayEntityType.ITEM || type == DisplayEntityType.TEXT;
    }

    @Override
    protected boolean isVisible(Player player, DisplayEntityData displayEntity) {
        return displayEntity.getType() != DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "#rrggbb|false";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1) {
            return false;
        }
        boolean noGlow = false;
        Color color = null;
        String colorHex = args.getNext();
        if (colorHex.equalsIgnoreCase("false")) {
            noGlow = true;
        } else if (colorHex.contains(",")) {
            // decimal r,g,b
            String[] colorParts = colorHex.split("\\,");
            if (colorParts.length == 3) {
                try {
                    int r = Integer.parseInt(colorParts[1]);
                    int g = Integer.parseInt(colorParts[2]);
                    int b = Integer.parseInt(colorParts[3]);
                    if (r >= 0 && r < 256 && g >= 0 && g < 256 && b >= 0 && b < 256) {
                        color = Color.fromRGB(r, g, b);
                    }
                } catch (NumberFormatException e) {
                }
            }
        } else {
            String colorHex2 = colorHex;
            if (colorHex2.startsWith("#")) {
                colorHex2 = colorHex2.substring(1);
            }
            try {
                long colorLong = Long.parseLong(colorHex2, 16);
                if (colorLong >= 0 && colorLong <= 0xffffff) {
                    color = Color.fromRGB((int) colorLong);
                }
            } catch (NumberFormatException e) {
            }
        }
        if (color == null && !noGlow) {
            Messages.sendError(player, "Ungültige Farbe: " + colorHex);
            return true;
        }

        if (noGlow) {
            displayEntity.getEntity().setGlowing(false);
        } else {
            displayEntity.getEntity().setGlowing(true);
            ((Display) displayEntity.getEntity()).setGlowColorOverride(color);
        }

        Component name = displayEntity.getNameAndOwner(player);
        if (noGlow) {
            Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("leuchtet nun nicht.")));
        } else {
            String hexColor = toHex(color);
            Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("leuchtet nun in der Farbe ")).append(Component.text(hexColor, TextColor.fromCSSHexString(hexColor))).append(Component.text(".")));
        }
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            Color bgColor = ((Display) displayEntity.getEntity()).getGlowColorOverride();
            if (bgColor != null) {
                return List.of("false", toHex(bgColor));
            } else {
                return List.of("false", "#ffffff");
            }
        }
        return List.of();
    }

    public static String toHex(Color c) {
        String hexString = Integer.toHexString(c.asRGB());
        while (hexString.length() < 6) {
            hexString = "0" + hexString;
        }
        return "#" + hexString;
    }
}
