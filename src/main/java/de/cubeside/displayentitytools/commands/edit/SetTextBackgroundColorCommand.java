package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

public class SetTextBackgroundColorCommand extends AbstractEditDisplayEntityCommand {
    public SetTextBackgroundColorCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return DisplayEntityType.TEXT;
    }

    @Override
    public String getUsage() {
        return "#aarrggbb|default";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1) {
            return false;
        }
        boolean defaultColor = false;
        Color color = null;
        String colorHex = args.getNext();
        if (colorHex.equalsIgnoreCase("default")) {
            defaultColor = true;
        } else if (colorHex.contains(",")) {
            // decimal a,r,g,b
            String[] colorParts = colorHex.split("\\,");
            if (colorParts.length == 4) {
                try {
                    int a = Integer.parseInt(colorParts[0]);
                    int r = Integer.parseInt(colorParts[1]);
                    int g = Integer.parseInt(colorParts[2]);
                    int b = Integer.parseInt(colorParts[3]);
                    if (a >= 0 && a < 256 && r >= 0 && r < 256 && g >= 0 && g < 256 && b >= 0 && b < 256) {
                        color = Color.fromARGB(a, r, g, b);
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
                if (colorLong >= 0 && colorLong <= 0xffffffffL) {
                    color = Color.fromARGB((int) colorLong);
                }
            } catch (NumberFormatException e) {
            }
        }
        if (color == null && !defaultColor) {
            player.sendMessage(Component.text("Ungültige Farbe: " + colorHex).color(NamedTextColor.RED));
            return true;
        }

        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der Position des Display-Entitys entfernt!").color(NamedTextColor.RED));
            return true;
        }
        if (defaultColor) {
            ((TextDisplay) displayEntity.getEntity()).setDefaultBackground(true);
        } else {
            ((TextDisplay) displayEntity.getEntity()).setDefaultBackground(false);
            deprecatedSetBackgroundColor((TextDisplay) displayEntity.getEntity(), color);
        }

        String name = displayEntity.getName() == null ? "" : "'" + displayEntity.getName() + "'";
        player.sendMessage(Component.text("Das Display-Entity " + name + " verwendet nun " + (defaultColor ? "die Standard-Hintergrundfarbe" : ("die Hintergrundfarbe " + toHex(color))) + ".").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            Color bgColor = deprecatedGetBackgroundColor((TextDisplay) displayEntity.getEntity());
            if (bgColor != null) {
                return List.of("default", toHex(bgColor));
            } else {
                return List.of("default", "#40000000");
            }
        }
        return List.of();
    }

    private String toHex(Color c) {
        String hexString = Integer.toHexString(c.asARGB());
        while (hexString.length() < 8) {
            hexString = "0" + hexString;
        }
        return "#" + hexString;
    }

    @SuppressWarnings("deprecation")
    private void deprecatedSetBackgroundColor(TextDisplay displayEntity, Color color) {
        displayEntity.setBackgroundColor(color);
    }

    @SuppressWarnings("deprecation")
    private Color deprecatedGetBackgroundColor(TextDisplay displayEntity) {
        return displayEntity.getBackgroundColor();
    }
}
