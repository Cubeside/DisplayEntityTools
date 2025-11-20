package de.cubeside.displayentitytools.commands;

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
import de.iani.playerUUIDCache.CachedPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ListCommand extends SubCommand {
    private DisplayEntityToolsPlugin plugin;

    public ListCommand(DisplayEntityToolsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public String getUsage() {
        return "[text|block|item|*] [radius] [angle] [owner|*]";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        Player player = (Player) sender;

        if (args.remaining() > 4) {
            return false;
        }

        DisplayEntityType type = null;
        if (args.hasNext()) {
            String ts = args.getNext("");
            if (!ts.equals("*")) {
                try {
                    type = DisplayEntityType.valueOf(ts.toUpperCase());
                } catch (IllegalArgumentException e) {
                    Messages.sendError(sender, "Ungültiger Typ: " + ts);
                    return true;
                }
            }
        }

        int radius = 10;
        if (args.hasNext()) {
            String as = args.getNext("");
            try {
                radius = Integer.parseInt(as);
            } catch (NumberFormatException e) {
                Messages.sendError(sender, "Ungültiger Radius (1...100): " + as);
                return true;
            }
            if (radius < 1 || radius > 100) {
                Messages.sendError(sender, "Ungültiger Radius (1...100): " + radius);
                return true;
            }
        }

        int angle = 360;
        if (args.hasNext()) {
            String as = args.seeNext("").toLowerCase();

            switch (as) {
                case "front":
                    angle = 180;
                    args.getNext("");
                    break;

                case "cursor":
                    angle = 10;
                    args.getNext("");
                    break;

                default:
                    if (as.length() <= 3) {
                        try {
                            angle = Integer.parseInt(as);
                            args.getNext("");
                        } catch (NumberFormatException e) {
                            // can't recognize this as an angle => ignore it
                            break;
                        }
                        if (angle < 0 || angle > 360) {
                            Messages.sendError(sender, "Ungültiger Winkel (0...360): " + angle);
                            return true;
                        }
                    }
                    break;
            }
        }

        UUID owner = plugin.isIgnoreDisplayEntityOwner() ? null : player.getUniqueId();
        boolean explicitAll = false;
        if (args.hasNext()) {
            String ownerString = args.getNext();
            if (ownerString.equals("*")) {
                owner = null;
                explicitAll = true;
            } else {
                CachedPlayer ownerCached = plugin.getPlayerUUIDCache().getPlayerFromNameOrUUID(ownerString);
                if (ownerCached == null) {
                    Messages.sendError(sender, "Spieler nicht gefunden: " + ownerString);
                    return true;
                }
                owner = ownerCached.getUniqueId();
            }
        }

        Class<? extends Entity> clazz = null;
        if (type != null) {
            clazz = type.getEntityClass();
        }

        Location playerLoc = player.getLocation();
        Location eyeLocation = player.getEyeLocation();
        ArrayList<DisplayEntityData> displayEntities = new ArrayList<>();
        {
            final ArrayList<Entity> entities = new ArrayList<>(clazz == null ? player.getWorld().getNearbyEntities(playerLoc, radius, radius, radius) : player.getWorld().getNearbyEntitiesByType(clazz, playerLoc, radius));
            for (Entity entity : entities) {
                if (clazz == null && (!(entity instanceof Display) && !(entity instanceof Interaction))) {
                    continue;
                }
                if (angle != 360) {
                    final Vector playerLookVector = playerLoc.getDirection();
                    final Vector entityDirectionVector = entity.getLocation().toVector().subtract(eyeLocation.toVector()).normalize();
                    final double dotProduct = playerLookVector.dot(entityDirectionVector);
                    final double angleToEntity = Math.toDegrees(Math.acos(dotProduct));
                    if (angleToEntity * 2 > angle) {
                        // Entity is outside the cone
                        continue;
                    }
                }

                final DisplayEntityData e = new DisplayEntityData(plugin, entity);
                if (owner != null && !e.getOwner().contains(owner)) {
                    continue;
                }
                if (!explicitAll && plugin.isIgnoreDisplayEntityOwner() && owner == null) {
                    if (!plugin.canEdit(player, e)) {
                        continue;
                    }
                }
                displayEntities.add(e);
            }
        }

        if (displayEntities.isEmpty()) {
            player.sendMessage(Component.text(""));
            Messages.sendError(sender, "Keine Display-Entites gefunden.");
            return true;
        }

        player.sendMessage(Component.text(""));
        Messages.sendSuccess(sender, Component.text(displayEntities.size() + " Display-Entities gefunden:").color(NamedTextColor.GOLD));
        displayEntities.sort((a, b) -> Double.compare(a.getLocation().distanceSquared(playerLoc), b.getLocation().distanceSquared(playerLoc)));
        for (DisplayEntityData e : displayEntities) {
            Component component = e.getShortDescription();
            component = component.clickEvent(ClickEvent.runCommand("/displayentity select " + e.getUUID()));
            component = component.hoverEvent(HoverEvent.showText(e.getDescription(player)));
            player.sendMessage(Component.text("  ").append(component));
        }
        return true;
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            ArrayList<String> list = Arrays.asList(DisplayEntityType.values()).stream().map(e -> e.name().toLowerCase()).collect(Collectors.toCollection(ArrayList::new));
            list.add("*");
            return list;
        } else if (args.remaining() == 2) {
            IntStream.range(1, 81).mapToObj(i -> Integer.toString(i)).toList();
        } else if (args.remaining() == 3) {
            ArrayList<String> list = plugin.getServer().getOnlinePlayers().stream().map(e -> e.getName()).collect(Collectors.toCollection(ArrayList::new));
            list.add("*");
            list.add("front");
            list.add("cursor");
            return list;
        } else if (args.remaining() == 4) {
            ArrayList<String> list = plugin.getServer().getOnlinePlayers().stream().map(e -> e.getName()).collect(Collectors.toCollection(ArrayList::new));
            list.add("*");
            return list;
        }
        return List.of();
    }
}
