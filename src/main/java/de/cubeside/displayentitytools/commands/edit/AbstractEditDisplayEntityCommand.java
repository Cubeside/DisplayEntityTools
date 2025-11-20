package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

public abstract class AbstractEditDisplayEntityCommand extends SubCommand {
    protected static final NumberFormat format = new DecimalFormat("#######0.0###", DecimalFormatSymbols.getInstance(Locale.US));
    protected final DisplayEntityToolsPlugin plugin;

    public AbstractEditDisplayEntityCommand(DisplayEntityToolsPlugin plugin) {
        this.plugin = plugin;
    }

    public DisplayEntityType getRequiredType() {
        return null;
    }

    public boolean hasRequiredType(DisplayEntityType type) {
        return getRequiredType() == null || getRequiredType() == type;
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public boolean isVisible(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        UUID editing = plugin.getCurrentEditingDisplayEntity(player.getUniqueId());
        if (editing == null) {
            return false;
        }
        DisplayEntityData displayEntity = null;
        Entity e = player.getWorld().getEntity(editing);
        if (!(e instanceof Display) && !(e instanceof Interaction)) {
            return false;
        }
        displayEntity = new DisplayEntityData(plugin, e);
        return (hasRequiredType(displayEntity.getType())) && isVisible(player, displayEntity);
    }

    protected boolean isVisible(Player player, DisplayEntityData displayEntity) {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        Player player = (Player) sender;
        UUID editing = plugin.getCurrentEditingDisplayEntity(player.getUniqueId());
        if (editing == null) {
            Messages.sendError(sender, "Du hast kein Display-Entity ausgewählt!");
            return true;
        }
        DisplayEntityData displayEntity = null;
        Entity e = player.getWorld().getEntity(editing);
        if (!(e instanceof Display) && !(e instanceof Interaction)) {
            Messages.sendError(sender, "Du hast kein Display-Entity ausgewählt!");
            return true;
        }
        displayEntity = new DisplayEntityData(plugin, e);
        if (!hasRequiredType(displayEntity.getType())) {
            Messages.sendError(sender, "Dieser Befehl ist für dieses Display-Entity nicht verfügbar!");
            return true;
        }
        if (!plugin.canEdit(player, displayEntity)) {
            Messages.sendError(sender, "Du hast keine Berechtigung, dieses Display-Entity zu bearbeiten!");
            return true;
        }
        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            Messages.sendError(player, "Du bist zu weit von der Position des Display-Entities entfernt!");
            return true;
        }
        return onEditDisplayEntityCommand(player, displayEntity, command, alias, commandString, args);
    }

    public abstract boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args);

    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        Player player = (Player) sender;
        UUID editing = plugin.getCurrentEditingDisplayEntity(player.getUniqueId());
        if (editing == null) {
            return List.of();
        }
        DisplayEntityData displayEntity = null;
        Entity e = player.getWorld().getEntity(editing);
        if (!(e instanceof Display) && !(e instanceof Interaction)) {
            return List.of();
        }
        displayEntity = new DisplayEntityData(plugin, e);
        if (!hasRequiredType(displayEntity.getType())) {
            return List.of();
        }
        if (!plugin.canEdit(player, displayEntity)) {
            return List.of();
        }

        return onDisplayEntityTabComplete(player, displayEntity, command, alias, args);
    }

    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        return List.of();
    }
}
