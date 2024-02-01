package de.cubeside.displayentitytools.commands;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
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
                    sender.sendMessage(Component.text("Ungültiger Typ: " + ts).color(NamedTextColor.RED));
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
                sender.sendMessage(Component.text("Ungültiger Radius (1...100): " + as).color(NamedTextColor.RED));
                return true;
            }
            if (radius < 1 || radius > 100) {
                sender.sendMessage(Component.text("Ungültiger Radius (1...100): " + radius).color(NamedTextColor.RED));
                return true;
            }
        }

        double angle = 360;
        if (args.hasNext()) {
            String as = args.seeNext("");

            switch (as) {
                case "vorn":
                case "front":
                    angle = 180;
                    args.getNext("");
                    break;

                case "cursor":
                    angle = 10;
                    args.getNext("");
                    break;

                default:
                    try {
                        angle = Double.parseDouble(as);
                        args.getNext("");
                    } catch (NumberFormatException e) {
                        // can't recognize this as an angle => ignore it
                        break;
                    }
                    if (angle < 0 || angle > 360) {
                        sender.sendMessage(Component.text("Ungültiger Winkel (0...360): " + angle).color(NamedTextColor.RED));
                        return true;
                    }
                    break;
            }
        }

        UUID owner = player.getUniqueId();
        if (args.hasNext()) {
            String ownerString = args.getNext();
            if (ownerString.equals("*")) {
                owner = null;
            } else {
                CachedPlayer ownerCached = plugin.getPlayerUUIDCache().getPlayerFromNameOrUUID(ownerString);
                if (ownerCached == null) {
                    sender.sendMessage(Component.text("Spieler nicht gefunden: " + ownerString).color(NamedTextColor.RED));
                    return true;
                }
                owner = ownerCached.getUniqueId();
            }
        }

        Class<? extends Display> clazz = Display.class;
        if (type != null) {
            clazz = type.getEntityClass();
        }

        Location playerLoc = player.getLocation();
        ArrayList<DisplayEntityData> displayEntities = new ArrayList<>();
        {
            final ArrayList<Display> entities = new ArrayList<>(player.getWorld().getNearbyEntitiesByType(clazz, playerLoc, radius));
            for (Display entity : entities) {
                if (angle != 360) {
                    final Vector playerLookVector = playerLoc.getDirection();
                    final Vector entityDirectionVector = entity.getLocation().toVector().subtract(playerLoc.toVector()).normalize();
                    final double dotProduct = playerLookVector.dot(entityDirectionVector);
                    final double angleToEntity = Math.toDegrees(Math.acos(dotProduct));
                    if (angleToEntity * 2 > angle) {
                        // Entity is outside the cone
                        continue;
                    }
                }

                final DisplayEntityData e = new DisplayEntityData(plugin, entity);
                if (owner != null && !owner.equals(e.getOwner())) {
                    continue;
                }
                displayEntities.add(e);
            }
        }

        if (displayEntities.isEmpty()) {
            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("Keine Display-Entites gefunden.").color(NamedTextColor.RED));
            return true;
        }

        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text(displayEntities.size() + " Display-Entities gefunden:").color(NamedTextColor.GOLD));
        displayEntities.sort((a, b) -> Double.compare(a.getLocation().distanceSquared(playerLoc), b.getLocation().distanceSquared(playerLoc)));
        for (DisplayEntityData e : displayEntities) {
            Component component = e.getShortDescription();
            component = component.clickEvent(ClickEvent.runCommand("/displayentity select " + e.getUUID()));
            component = component.hoverEvent(HoverEvent.showText(e.getDescription(player)));
            player.sendMessage(component);
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
            return list;
        }
        return List.of();
    }
}
