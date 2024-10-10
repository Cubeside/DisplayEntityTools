package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.commands.ArgsParser;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class InfoCommand extends AbstractEditDisplayEntityCommand {
    public InfoCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return null;
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() > 0) {
            return false;
        }

        NumberFormat nf = new DecimalFormat("######0.#####", DecimalFormatSymbols.getInstance(Locale.US));

        Display entity = displayEntity.getEntity();
        String typeName = displayEntity.getType().getDisplayName();
        Component c = Component.text("Infos zum " + typeName + " ", NamedTextColor.GOLD).append(displayEntity.getColoredName());
        Messages.sendSuccess(player, c);
        player.sendMessage(Component.text("").append(Component.text("  Inhalt: ", NamedTextColor.AQUA)).append(displayEntity.getShortContentInfo()));

        player.sendMessage(Component.text("").append(Component.text("  Besitzer: ", NamedTextColor.AQUA)).append(displayEntity.getOwner().size() == 0 ? Component.text("niemand", null, TextDecoration.ITALIC) : displayEntity.getColoredOwners()));
        player.sendMessage(Component.empty());
        Location loc = entity.getLocation();
        player.sendMessage(Component.text("").append(Component.text("  Position: ", NamedTextColor.AQUA)).append(Component.text(nf.format(loc.getX()) + " " + nf.format(loc.getY()) + " " + nf.format(loc.getZ()))));
        player.sendMessage(Component.text("").append(Component.text("  Rotation: ", NamedTextColor.AQUA)).append(Component.text(nf.format(loc.getYaw()) + " " + nf.format(loc.getPitch()))));
        player.sendMessage(Component.text("").append(Component.text("  Billboard-Mode: ", NamedTextColor.AQUA)).append(Component.text(StringUtil.capitalizeFirstLetter(entity.getBillboard().name(), true))));

        Transformation transformation = entity.getTransformation();
        AxisAngle4f leftRoation = new AxisAngle4f(transformation.getLeftRotation());
        AxisAngle4f rightRoation = new AxisAngle4f(transformation.getRightRotation());
        Vector3f translation = transformation.getTranslation();
        Vector3f scale = transformation.getScale();
        player.sendMessage(Component.text("").append(Component.text("  Right Rotation: ", NamedTextColor.AQUA)).append(Component.text(nf.format(rightRoation.x) + " " + nf.format(rightRoation.y) + " " + nf.format(rightRoation.z) + " " + nf.format(Math.toDegrees(rightRoation.angle)))));
        player.sendMessage(Component.text("").append(Component.text("  Scale: ", NamedTextColor.AQUA)).append(Component.text(nf.format(scale.x()) + " " + nf.format(scale.y()) + " " + nf.format(scale.z()))));
        player.sendMessage(Component.text("").append(Component.text("  Left Rotation: ", NamedTextColor.AQUA)).append(Component.text(nf.format(leftRoation.x) + " " + nf.format(leftRoation.y) + " " + nf.format(leftRoation.z) + " " + nf.format(Math.toDegrees(leftRoation.angle)))));
        player.sendMessage(Component.text("").append(Component.text("  Translation: ", NamedTextColor.AQUA)).append(Component.text(nf.format(translation.x()) + " " + nf.format(translation.y()) + " " + nf.format(translation.z()))));
        player.sendMessage(Component.empty());
        Brightness brightness = entity.getBrightness();
        player.sendMessage(Component.text("").append(Component.text("  Licht: ", NamedTextColor.AQUA)).append(Component.text(brightness == null ? "default" : ("Block: " + brightness.getBlockLight() + " Umgebung: " + brightness.getSkyLight()))));
        Color glowColor = entity.getGlowColorOverride();
        player.sendMessage(Component.text("").append(Component.text("  Leuchtrahmen: ", NamedTextColor.AQUA)).append(Component.text(!entity.isGlowing() ? "-" : (glowColor == null ? "default" : SetGlowingCommand.toHex(glowColor)))));
        player.sendMessage(Component.text("").append(Component.text("  Schatten: ", NamedTextColor.AQUA)).append(Component.text("Radius: " + nf.format(entity.getShadowRadius()) + " Stärke: " + nf.format(entity.getShadowStrength()))));
        if (entity instanceof TextDisplay text) {
            player.sendMessage(Component.empty());
            player.sendMessage(Component.text("").append(Component.text("  Zeilen: ", NamedTextColor.AQUA)).append(Component.text("Breite: " + nf.format(text.getLineWidth()) + " Ausrichtung: " + StringUtil.capitalizeFirstLetter(text.getAlignment().name(), true))));
            Color bgColor = text.getBackgroundColor();
            int alpha = ((TextDisplay) displayEntity.getEntity()).getTextOpacity() & 0xff;
            player.sendMessage(Component.text("").append(Component.text("  Anzeige: ", NamedTextColor.AQUA)).append(Component.text("Hintergrund: " + (bgColor == null ? "default" : SetTextBackgroundColorCommand.toHex(bgColor)) + " Alpha: " + alpha + " Shadow: " + text.isShadowed())));
            player.sendMessage(Component.text("").append(Component.text("  Text:", NamedTextColor.AQUA)));
            player.sendMessage(text.text());
        }
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        return List.of();
    }
}
