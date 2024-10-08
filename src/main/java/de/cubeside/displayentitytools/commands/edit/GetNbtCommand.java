package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPermissions;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.cubeside.displayentitytools.util.Messages;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class GetNbtCommand extends AbstractEditDisplayEntityCommand {
    public GetNbtCommand(DisplayEntityToolsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getRequiredPermission() {
        return DisplayEntityToolsPermissions.PERMISSION_GETNBT;
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
        if (args.remaining() != 0) {
            return false;
        }
        String nbt = plugin.getNmsUtils().getNbtUtils().writeString(plugin.getNmsUtils().getEntityUtils().getNbt(displayEntity.getEntity()));

        Component name = displayEntity.getNameAndOwner(player);
        Messages.sendSuccess(player, Component.text("Das Display-Entity ").append(name).append(Component.text("hat folgenden NBT-Inhalt (anklicken zum Kopieren):")));
        player.sendMessage(Component.text(nbt).clickEvent(ClickEvent.copyToClipboard(nbt)).hoverEvent(HoverEvent.showText(Component.text("Anklicken zum Kopieren"))));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        return List.of();
    }
}
