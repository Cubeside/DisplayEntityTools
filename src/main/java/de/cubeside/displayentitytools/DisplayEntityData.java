package de.cubeside.displayentitytools;

import de.iani.cubesideutils.StringUtil;
import de.iani.playerUUIDCache.CachedPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
    private Set<UUID> owner = Set.of();
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
        if (conf.isList("owner")) {
            HashSet<UUID> owners = new HashSet<>();
            for (String ownerId : conf.getStringList("owner")) {
                try {
                    owners.add(UUID.fromString(ownerId));
                } catch (IllegalArgumentException e1) {
                    plugin.getLogger().log(Level.SEVERE, "owner is not a uuid: " + ownerId, e1);
                }
            }
            if (owners.isEmpty()) {
                owner = Set.of();
            } else if (owners.size() == 1) {
                owner = Set.of(owners.iterator().next());
            } else {
                owner = Collections.unmodifiableSet(owners);
            }
        } else {
            String ownerId = conf.getString("owner");
            if (ownerId != null) {
                try {
                    owner = Set.of(UUID.fromString(ownerId));
                } catch (IllegalArgumentException e1) {
                    plugin.getLogger().log(Level.SEVERE, "owner is not a uuid: " + ownerId, e1);
                }
            }
        }
        if (owner == null) {
            owner = Set.of();
        }
        name = conf.getString("name");
    }

    private void saveToEntity() {
        YamlConfiguration conf = new YamlConfiguration();
        if (owner != null && !owner.isEmpty()) {
            if (owner.size() == 1) {
                conf.set("owner", owner.iterator().next().toString());
            } else {
                ArrayList<String> ownerList = new ArrayList<>();
                for (UUID o : owner) {
                    ownerList.add(o.toString());
                }
                conf.set("owner", ownerList);
            }
        }
        conf.set("name", name);
        display.getPersistentDataContainer().set(plugin.getDataNamespacedKey(), PersistentDataType.STRING, conf.saveToString());
    }

    public Set<UUID> getOwner() {
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
        Component extra = getShortContentInfo();
        if (extra != null && extra != Component.empty()) {
            main = main.append(Component.text(" (").append(extra.color(NamedTextColor.GREEN)).append(Component.text(")")).color(NamedTextColor.WHITE));
        }
        return main;
    }

    public Component getShortContentInfo() {
        Component extra = Component.empty();
        if (type == DisplayEntityType.TEXT) {
            String textString = LegacyComponentSerializer.legacySection().serialize(((TextDisplay) display).text());
            int firstNewline = textString.indexOf("\n");
            if (firstNewline >= 0) {
                textString = textString.substring(0, Math.min(firstNewline, 25)) + "...";
            } else if (textString.length() > 28) {
                textString = textString.substring(0, 25) + "...";
            }
            extra = Component.text(StringUtil.stripColors(StringUtil.revertColors(textString)));
        } else if (type == DisplayEntityType.BLOCK) {
            extra = Component.text(((BlockDisplay) display).getBlock().getAsString());
        } else if (type == DisplayEntityType.ITEM) {
            ItemStack stack = ((ItemDisplay) display).getItemStack();
            Material m = stack == null ? Material.AIR : stack.getType();
            extra = Component.text(m.getKey().asString());
        }
        return extra;
    }

    public Component getDescription(Player player) {
        Component name = getName(NamedTextColor.AQUA);
        Component descr = Component.text("Display-Entity ", NamedTextColor.WHITE).append(name);
        if (owner != null && !owner.isEmpty()) {
            Component ownerLine = Component.text("Besitzer: ");
            boolean first = true;
            for (UUID ownerId : owner) {
                CachedPlayer ownerPlayer = plugin.getPlayerUUIDCache().getPlayer(ownerId);
                if (!first) {
                    ownerLine = ownerLine.append(Component.text(", "));
                }
                first = false;
                ownerLine = ownerLine.append(Component.text(ownerPlayer != null ? ownerPlayer.getName() : ownerId.toString()).color(NamedTextColor.AQUA));
            }
            descr = descr.appendNewline().append(ownerLine);
        }
        descr = descr.appendNewline().append(Component.text("Typ: ").append(Component.text(StringUtil.capitalizeFirstLetter(type.name(), true)).color(NamedTextColor.AQUA)));
        return descr;
    }

    public Component getName(TextColor color) {
        Component name = Component.empty();
        if (getName() != null) {
            name = name.append(Component.text("'")).append(Component.text(getName(), color)).append(Component.text("' "));
        }
        return name;

    }

    public Component getNameAndOwner() {
        return getNameAndOwner(null);
    }

    public Component getColoredName() {
        return getName(TextColor.fromCSSHexString("#a0ffa0"));
    }

    public Component getNameAndOwner(Player player) {
        Component name = getColoredName();
        if (!getOwner().isEmpty() && (player == null || !getOwner().contains(player.getUniqueId()))) {
            name = name.append(Component.text("von ").append(getColoredOwners())).append(Component.space());
        }
        return name;
    }

    public Component getColoredOwners() {
        Component name = Component.empty();
        boolean first = true;
        for (UUID ownerId : getOwner()) {
            CachedPlayer cp = plugin.getPlayerUUIDCache().getPlayer(ownerId);
            if (!first) {
                name = name.append(Component.text(", "));
            }
            name = name.append(Component.text(cp != null ? cp.getName() : ownerId.toString(), NamedTextColor.WHITE));
            first = false;
        }
        return name;
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

    public void addOwners(Set<UUID> uuids) {
        HashSet<UUID> newOwner = new HashSet<>();
        newOwner.addAll(owner);
        newOwner.addAll(uuids);
        if (newOwner.isEmpty()) {
            owner = Set.of();
        } else if (newOwner.size() == 1) {
            owner = Set.of(newOwner.iterator().next());
        } else {
            owner = Collections.unmodifiableSet(newOwner);
        }
        saveToEntity();
    }

    public void removeOwners(HashSet<UUID> uuids) {
        HashSet<UUID> newOwner = new HashSet<>();
        newOwner.addAll(owner);
        newOwner.removeAll(uuids);
        if (newOwner.isEmpty()) {
            owner = Set.of();
        } else if (newOwner.size() == 1) {
            owner = Set.of(newOwner.iterator().next());
        } else {
            owner = Collections.unmodifiableSet(newOwner);
        }
        saveToEntity();
    }
}
