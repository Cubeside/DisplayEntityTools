package de.cubeside.displayentitytools.commands;

import de.cubeside.displayentitytools.DisplayEntityToolsPermissions;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.bukkit.items.ItemStacks;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetItemCommand extends SubCommand {
    private DisplayEntityToolsPlugin plugin;

    public GetItemCommand(DisplayEntityToolsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public String getRequiredPermission() {
        return DisplayEntityToolsPermissions.PERMISSION_GETITEM;
    }

    @Override
    public String getUsage() {
        return "<text|block|item> [amount]";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        if (args.remaining() == 0 || args.remaining() > 2) {
            return false;
        }
        DisplayEntityType type;
        {
            String ts = args.getNext("");
            try {
                type = DisplayEntityType.valueOf(ts.toUpperCase());
            } catch (IllegalArgumentException e) {
                Messages.sendError(sender, "Ungültiger Typ: " + ts);
                return true;
            }
        }
        int amount = 1;
        if (args.hasNext()) {
            String as = args.getNext("");
            try {
                amount = Integer.parseInt(as);
            } catch (NumberFormatException e) {
                Messages.sendError(sender, "Ungültige Anzahl: " + as);
                return true;
            }
        }
        if (amount < 1 || amount > 64) {
            Messages.sendError(sender, "Ungültige Anzahl: " + amount);
            return true;
        }
        ItemStack stack = ItemStacks.amount(plugin.getSpawnerItem(type), amount);
        if (ItemStacks.addToInventoryIfFits(((Player) sender).getInventory(), stack)) {
            Messages.sendSuccess(sender, amount + " Display-Entity-Spawnitems erhalten!");
        } else {
            Messages.sendError(sender, "Du hast nicht genügend Platz im Inventar.");
        }
        return true;
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            return Arrays.asList(DisplayEntityType.values()).stream().map(e -> e.name().toLowerCase()).toList();
        } else if (args.remaining() == 2) {
            IntStream.range(1, 65).mapToObj(i -> Integer.toString(i)).toList();
        }
        return List.of();
    }
}
