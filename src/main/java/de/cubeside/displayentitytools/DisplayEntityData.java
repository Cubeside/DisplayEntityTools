package de.cubeside.displayentitytools;

import de.cubeside.displayentitytools.commands.edit.AbstractEditDisplayEntityCommand;
import de.iani.cubesideutils.StringUtil;
import de.iani.playerUUIDCache.CachedPlayer;
import java.util.UUID;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class DisplayEntityData {
    private final DisplayEntityToolsPlugin plugin;
    private final Display display;
    private final DisplayEntityType type;
    private UUID owner;
    private String name;
    private Location location;

    public DisplayEntityData(DisplayEntityToolsPlugin plugin, Display display) {
        this.plugin = plugin;
        this.display = display;
        this.type = DisplayEntityType.getByClass(display.getClass());
        String confString = display.getPersistentDataContainer().get(plugin.getDataNamespacedKey(), PersistentDataType.STRING);
        if (confString == null) {
            return;
        }
        YamlConfiguration conf = new YamlConfiguration();
        try {
            conf.loadFromString(confString);
        } catch (InvalidConfigurationException e1) {
            plugin.getLogger().log(Level.SEVERE, "Could not load Display data", e1);
            return;
        }
        String ownerId = conf.getString("owner");
        if (ownerId != null) {
            try {
                owner = UUID.fromString(ownerId);
            } catch (IllegalArgumentException e1) {
                plugin.getLogger().log(Level.SEVERE, "owner is not a uuid: " + ownerId, e1);
            }
        }
        name = conf.getString("name");
    }

    private void saveToEntity() {
        YamlConfiguration conf = new YamlConfiguration();
        if (owner != null) {
            conf.set("owner", owner.toString());
        }
        conf.set("name", name);
        display.getPersistentDataContainer().set(plugin.getDataNamespacedKey(), PersistentDataType.STRING, conf.saveToString());
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        if (location == null) {
            location = display.getLocation();
        }
        return location;
    }

    public UUID getUUID() {
        return display.getUniqueId();
    }

    public Component getShortDescription() {
        String typeString = StringUtil.capitalizeFirstLetter(type.name(), true);
        String eName = typeString;
        if (name != null) {
            eName += " " + name;
        }
        Component main = Component.text(eName).color(NamedTextColor.AQUA);
        Component extra = null;
        if (type == DisplayEntityType.TEXT) {
            String textString = LegacyComponentSerializer.legacySection().serialize(((TextDisplay) display).text());
            int firstNewline = textString.indexOf("\n");
            if (firstNewline >= 0) {
                textString = textString.substring(0, firstNewline) + "...";
            }
            extra = Component.text(StringUtil.stripColors(StringUtil.revertColors(textString)));
        } else if (type == DisplayEntityType.BLOCK) {
            extra = Component.text(((BlockDisplay) display).getBlock().getAsString());
        } else if (type == DisplayEntityType.ITEM) {
            ItemStack stack = ((ItemDisplay) display).getItemStack();
            Material m = stack == null ? Material.AIR : stack.getType();
            extra = Component.text(m.getKey().asString());
        }
        if (extra != null) {
            main = main.append(Component.text(" (").append(extra.color(NamedTextColor.GREEN)).append(Component.text(")")).color(NamedTextColor.WHITE));
        }
        return main;
    }

    public Component getDescription(Player player) {
        String name = AbstractEditDisplayEntityCommand.getNameAndOwner(plugin, player, this);
        Component descr = Component.text("Display-Entity " + name).color(NamedTextColor.WHITE);
        if (owner != null) {
            CachedPlayer ownerPlayer = plugin.getPlayerUUIDCache().getPlayer(owner);
            descr = descr.appendNewline().append(Component.text("Besitzer: ").append(Component.text(ownerPlayer != null ? ownerPlayer.getName() : owner.toString()).color(NamedTextColor.AQUA)));
        }
        descr = descr.appendNewline().append(Component.text("Typ: ").append(Component.text(StringUtil.capitalizeFirstLetter(type.name(), true)).color(NamedTextColor.AQUA)));
        return descr;
    }

    public String getName() {
        return name;
    }

    public DisplayEntityType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
        saveToEntity();
    }

    public void teleport(Location location) {
        display.teleport(location);
    }

    public Display getEntity() {
        return display;
    }
}
