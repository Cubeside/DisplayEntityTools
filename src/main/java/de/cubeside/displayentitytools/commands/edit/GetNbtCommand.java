package de.cubeside.displayentitytools.commands.edit;

import de.cubeside.displayentitytools.DisplayEntityData;
import de.cubeside.displayentitytools.DisplayEntityToolsPermissions;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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

        String name = getNameAndOwner(player, displayEntity);
        player.sendMessage(Component.text("Das Display-Entity " + name + "hat folgenden NBT-Inhalt (anklicken zum Kopieren):").color(NamedTextColor.GREEN));
        player.sendMessage(Component.text(nbt).clickEvent(ClickEvent.copyToClipboard(nbt)).hoverEvent(HoverEvent.showText(Component.text("Anklicken zum Kopieren"))));
        return true;
    }

    @Override
    public Collection<String> onDisplayEntityTabComplete(Player player, DisplayEntityData displayEntity, Command command, String alias, ArgsParser args) {
        return List.of();
    }
}
