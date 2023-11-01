package de.cubeside.displayentitytools.commands.edit;

import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetBlockCommand extends AbstractEditDisplayEntityCommand {
    private ArrayList<String> materialNames;

    public SetBlockCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);

        materialNames = new ArrayList<>();
        for (Material m : Material.values()) {
            if (m.isBlock() && m != Material.AIR) {
                materialNames.add(m.getKey().getKey());
            }
        }
    }

    @Override
    public DisplayEntityType getRequiredType() {
        return DisplayEntityType.BLOCK;
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
        BlockData block;
        if (args.remaining() == 1) {
            try {
                block = Bukkit.createBlockData(args.getNext());
            } catch (IllegalArgumentException e) {
                player.sendMessage(Component.text("Ungültiger Block!").color(NamedTextColor.RED));
                return true;
            }
        } else {
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (stack != null) {
                if (!stack.getType().isBlock()) {
                    player.sendMessage(Component.text("Ungültiger Block!").color(NamedTextColor.RED));
                    return true;
                }
                block = stack.getType().createBlockData();
            } else {
                block = Material.AIR.createBlockData();
            }
        }
        if (block.getMaterial() == Material.AIR) {
            player.sendMessage(Component.text("Ungültiger Block!").color(NamedTextColor.RED));
            return true;
        }

        if (displayEntity.getLocation().distanceSquared(player.getLocation()) > 100 * 100) {
            player.sendMessage(Component.text("Du bist zu weit von der Position des Display-Entities entfernt!").color(NamedTextColor.RED));
            return true;
        }
        ((BlockDisplay) displayEntity.getEntity()).setBlock(block);

        String name = getNameAndOwner(player, displayEntity);
        player.sendMessage(Component.text("Block für das Display-Entity " + name + "wurde gesetzt.").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            String block = args.getNext();
            int stateStart = block.indexOf("[");
            String baseBlock = stateStart < 0 ? block : block.substring(0, stateStart);
            boolean hasNamespace = baseBlock.contains(":");
            BlockType type = BlockType.REGISTRY.get(baseBlock.toLowerCase());
            if (type != null) {
                ArrayList<String> states = new ArrayList<>();
                for (BlockState state : type.getAllStates()) {
                    String stateString = state.getAsString();
                    if (!hasNamespace) {
                        int nameSpaceEnd = stateString.indexOf(":");
                        if (nameSpaceEnd >= 0) {
                            stateString = stateString.substring(nameSpaceEnd + 1);
                        }
                    }
                    states.add(stateString);
                }
                return states;
            }
            return materialNames;
        }
        return List.of();
    }
}
