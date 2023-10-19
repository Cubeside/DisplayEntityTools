package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetItemCommand extends AbstractEditDisplayEntityCommand {
    private ArrayList<String> materialNames;

    public SetItemCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);

        materialNames = new ArrayList<>();
        for (Material m : Material.values()) {
            if (m.isItem() && m != Material.AIR) {
                materialNames.add(m.getKey().getKey());
            }
        }
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return DisplayEntityType.ITEM;
    }

    @Override
    public String getUsage() {
        return "[item]";
    }

    @Override
    public boolean onEditDisplayEntityCommand(Player player, DisplayEntityData displayEntity, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() > 1) {
            return false;
        }
        ItemStack stack;
        if (args.remaining() == 1) {
            Material mat = Material.matchMaterial(args.getNext());
            if (mat == null || !mat.isItem()) {
                player.sendMessage(Component.text("Ungültiges Item!").color(NamedTextColor.RED));
                return true;
            }
            stack = new ItemStack(mat);
        } else {
            stack = player.getInventory().getItemInMainHand();
        }
        if (stack.getType() == Material.AIR) {
            player.sendMessage(Component.text("Ungültiges Item!").color(NamedTextColor.RED));
            return true;
        }

        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der Position des Display-Entitys entfernt!").color(NamedTextColor.RED));
            return true;
        }
        ((ItemDisplay) displayEntity.getEntity()).setItemStack(stack);

        String name = getNameAndOwner(player, displayEntity);
        player.sendMessage(Component.text("Item für das Display-Entity " + name + "wurde gesetzt.").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            return materialNames;
        }
        return List.of();
    }
}
