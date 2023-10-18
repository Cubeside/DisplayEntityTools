package de.cubeside.displayentitytools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PlayerListener implements Listener {
    private DisplayEntityToolsPlugin plugin;

    public PlayerListener(DisplayEntityToolsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.useItemInHand() == Result.DENY) {
            return;
        }
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        ItemStack inHand = event.getPlayer().getInventory().getItem(event.getHand());
        DisplayEntityType typeToSpawn = plugin.getDisplayEntityType(inHand);
        if (typeToSpawn == null) {
            return;
        }
        event.setCancelled(true);
        Location spawnLocation = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
        if (typeToSpawn != DisplayEntityType.BLOCK) {
            spawnLocation.add(0.5, 0.5, 0.5);
        }
        if (plugin.getWorldGuardHelper() != null) {
            if (!plugin.getWorldGuardHelper().canBuild(event.getPlayer(), spawnLocation)) {
                event.getPlayer().sendMessage(Component.text("Du hast hier keine Baurechte.").color(NamedTextColor.RED));
                return;
            }
        }
        Display display = null;
        if (typeToSpawn == DisplayEntityType.TEXT) {
            display = spawnLocation.getWorld().spawn(spawnLocation, TextDisplay.class, e -> {
                e.text(Component.text("Neues Textdisplay"));
                e.setBillboard(Billboard.VERTICAL);
            });
        } else if (typeToSpawn == DisplayEntityType.ITEM) {
            display = spawnLocation.getWorld().spawn(spawnLocation, ItemDisplay.class, e -> {
                e.setItemStack(new ItemStack(Material.REDSTONE_BLOCK));
            });
        } else if (typeToSpawn == DisplayEntityType.BLOCK) {
            display = spawnLocation.getWorld().spawn(spawnLocation, BlockDisplay.class, e -> {
                e.setBlock(Material.BEDROCK.createBlockData());
            });
        } else {
            throw new RuntimeException("Unknown type: " + typeToSpawn);
        }
        if (display != null) {
            YamlConfiguration conf = new YamlConfiguration();
            conf.set("owner", event.getPlayer().getUniqueId().toString());
            display.getPersistentDataContainer().set(plugin.getDataNamespacedKey(), PersistentDataType.STRING, conf.saveToString());
            plugin.setCurrentEditingDisplayEntity(event.getPlayer().getUniqueId(), display.getUniqueId());
            event.getPlayer().sendMessage(Component.text("Das Display-Entity wurde gespawnt und ausgewählt.").color(NamedTextColor.GREEN));
        } else {
            event.getPlayer().sendMessage(Component.text("Das Display-Entity konnte nicht gespawnt werden.").color(NamedTextColor.RED));
            return;
        }
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            // consume item
            if (inHand.getAmount() <= 1) {
                event.getPlayer().getInventory().setItem(event.getHand(), null);
            } else {
                inHand = new ItemStack(inHand);
                inHand.setAmount(inHand.getAmount() - 1);
                event.getPlayer().getInventory().setItem(event.getHand(), inHand);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (plugin.getDisplayEntityType(event.getPlayer().getInventory().getItem(event.getHand())) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        if (plugin.getDisplayEntityType(event.getPlayer().getInventory().getItem(event.getHand())) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerArmorStandManipulateEvent event) {
        if (plugin.getDisplayEntityType(event.getPlayer().getInventory().getItem(event.getHand())) != null) {
            event.setCancelled(true);
        }
    }
}
