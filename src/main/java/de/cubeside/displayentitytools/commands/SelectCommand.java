package de.cubeside.displayentitytools.commands;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

public class SelectCommand extends SubCommand {
    private DisplayEntityToolsPlugin plugin;

    public SelectCommand(DisplayEntityToolsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public String getUsage() {
        return "[name]";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        Player player = (Player) sender;
        if (args.remaining() < 1) {
            Messages.sendSuccess(player, Component.text("Die Display-Entity-Auswahl wurde aufgehoben!"));
            plugin.setCurrentEditingDisplayEntity(player.getUniqueId(), null);
            return true;
        }
        String nameOrUUID = args.getAll("");
        DisplayEntityData displayEntity = null;
        try {
            Entity e = player.getWorld().getEntity(UUID.fromString(nameOrUUID));
            if (e instanceof Display d) {
                displayEntity = new DisplayEntityData(plugin, d);
            } else if (e instanceof Interaction d) {
                displayEntity = new DisplayEntityData(plugin, d);
            }
        } catch (IllegalArgumentException e) {
            ArrayList<Entity> entities = new ArrayList<>(player.getWorld().getNearbyEntities(player.getLocation(), 80, 80, 80));
            for (Entity entity : entities) {
                if (!(entity instanceof Display) && !(entity instanceof Interaction)) {
                    continue;
                }
                DisplayEntityData data = new DisplayEntityData(plugin, entity);
                if (nameOrUUID.equalsIgnoreCase(data.getName()) && plugin.canEdit(player, data)) {
                    displayEntity = data;
                    break;
                }
            }
        }
        if (displayEntity == null) {
            Messages.sendError(player, "Display-Entity nicht gefunden.");
            return true;
        }
        if (!plugin.canEdit(player, displayEntity)) {
            Messages.sendError(player, "Du hast keine Berechtigung, dieses Display-Entity zu bearbeiten.");
            return true;
        }
        Component name = displayEntity.getNameAndOwner(player);
        Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("wurde ausgewählt!")));
        plugin.setCurrentEditingDisplayEntity(player.getUniqueId(), displayEntity);
        return true;
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }
        if (args.remaining() == 1) {
            ArrayList<String> names = new ArrayList<>();
            String name = args.getAll("").trim();
            ArrayList<Entity> entities = new ArrayList<>(player.getWorld().getNearbyEntities(player.getLocation(), 80, 80, 80));
            for (Entity entity : entities) {
                if (!(entity instanceof Display) && !(entity instanceof Interaction)) {
                    continue;
                }
                DisplayEntityData data = new DisplayEntityData(plugin, entity);
                if (plugin.canEdit(player, data) && data.getName() != null && data.getName().startsWith(name)) {
                    names.add(data.getName());
                }
            }
            return names;
        }
        return List.of();
    }
}
