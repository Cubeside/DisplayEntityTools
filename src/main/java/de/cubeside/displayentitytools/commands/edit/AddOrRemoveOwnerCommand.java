package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.commands.ArgsParser;
import de.iani.playerUUIDCache.CachedPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class AddOrRemoveOwnerCommand extends AbstractEditDisplayEntityCommand {
    private final boolean add;

    public AddOrRemoveOwnerCommand(DisplayEntityToolsPlugin plugin, boolean add) {
        super(plugin);
        this.add = add;
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return null;
    }

    @Override
    public String getUsage() {
        return "<player>";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() < 1) {
            return false;
        }
        HashSet<UUID> ownersToAdd = new HashSet<>();
        while (args.hasNext()) {
            String ownerName = args.getNext();
            CachedPlayer owner = plugin.getPlayerUUIDCache().getPlayerFromNameOrUUID(ownerName);
            if (owner == null) {
                Messages.sendError(player, "Unbekannter Spieler: " + ownerName);
            } else {
                ownersToAdd.add(owner.getUUID());
            }
        }
        if (!ownersToAdd.isEmpty()) {
            if (add) {
                displayEntity.addOwners(ownersToAdd);
            } else {
                displayEntity.removeOwners(ownersToAdd);
            }
            String name = "";
            if (displayEntity.getName() != null) {
                name = "'" + displayEntity.getName() + "' ";
            }

            Component ownerComp = Component.text("");
            boolean first = true;
            for (UUID ownerId : displayEntity.getOwner()) {
                if (!first) {
                    ownerComp = ownerComp.append(Component.text(", "));
                }
                first = false;
                CachedPlayer cp = plugin.getPlayerUUIDCache().getPlayer(ownerId);
                ownerComp = ownerComp.append(Component.text(cp != null ? cp.getName() : ownerId.toString(), NamedTextColor.WHITE));
            }

            if (displayEntity.getOwner().size() == 0) {
                Messages.sendSuccess(player, "Das Display-Entity " + name + "hat nun keinen Besitzer.");
            } else {
                Messages.sendSuccess(player, Component.text("Das Display-Entity " + name + "hat nun " + (displayEntity.getOwner().size() == 1 ? "den" : "die") + " Besitzer: ").append(ownerComp));
            }
        }
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (add) {
            return null;
        }
        Set<String> existing = new HashSet<>();
        while (args.hasNext()) {
            existing.add(args.getNext().toLowerCase());
        }
        ArrayList<String> result = new ArrayList<>();
        for (UUID ownerId : displayEntity.getOwner()) {
            CachedPlayer cp = plugin.getPlayerUUIDCache().getPlayer(ownerId);
            String s = cp != null ? cp.getName() : ownerId.toString();
            if (!existing.contains(s.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }
}
