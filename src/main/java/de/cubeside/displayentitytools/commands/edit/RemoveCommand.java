package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.bukkit.items.ItemStacks;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RemoveCommand extends AbstractEditDisplayEntityCommand {
    public RemoveCommand(DisplayEntityToolsPlugin plugin) {
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
        ItemStack spawnerItem = plugin.getSpawnerItem(displayEntity.getType());
        boolean added = ItemStacks.addToInventoryIfFits(player.getInventory(), spawnerItem);
        if (added || player.getGameMode() == GameMode.CREATIVE) {
            displayEntity.getEntity().remove();
            plugin.setCurrentEditingDisplayEntity(player.getUniqueId(), null);
            String name = getNameAndOwner(player, displayEntity);
            if (added) {
                player.sendMessage(Component.text("Das Display-Entity " + name + "wurde entfernt und befindet sich wieder in deinem Inventar.").color(NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("Das Display-Entity " + name + "wurde entfernt.").color(NamedTextColor.GREEN));
            }
        } else {
            player.sendMessage(Component.text("Du hast keinen Platz im Inventar frei.").color(NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        return List.of();
    }
}
