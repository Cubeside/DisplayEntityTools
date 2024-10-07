package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.commands.ArgsParser;
import de.iani.playerUUIDCache.CachedPlayer;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

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
        String name = "";
        if (displayEntity.getName() != null) {
            name = "'" + displayEntity.getName() + "' ";
        }

        StringBuilder owners = new StringBuilder();
        boolean first = true;
        for (UUID ownerId : displayEntity.getOwner()) {
            if (!first) {
                owners.append(", ");
            }
            first = false;
            CachedPlayer cp = plugin.getPlayerUUIDCache().getPlayer(ownerId);
            if (cp != null) {
                owners.append(cp.getName());
            } else {
                owners.append(ownerId.toString());
            }
        }
        if (displayEntity.getOwner().size() == 0) {
            player.sendMessage(Component.text("Das Display-Entity " + name + "hat keinen Besitzer.").color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Das Display-Entity " + name + "hat " + (displayEntity.getOwner().size() == 1 ? "den" : "die") + " Besitzer: " + owners.toString()).color(NamedTextColor.GREEN));
        }

        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        return List.of();
    }
}
