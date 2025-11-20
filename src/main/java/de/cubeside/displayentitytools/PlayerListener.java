package de.cubeside.displayentitytools;

import de.cubeside.displayentitytools.util.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PlayerListener implements Listener {
    private DisplayEntityToolsPlugin plugin;

    public PlayerListener(DisplayEntityToolsPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    private boolean isInteractableSuppressDeprecation(Material m) {
        return m.isInteractable();
    }

    private boolean isRealInteractable(Block block) {
        if (!isInteractableSuppressDeprecation(block.getType())) {
            return false;
        }
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Stairs || blockData instanceof Fence) {
            return false;
        }
        return true;
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
        if (!event.hasBlock()) {
            event.setCancelled(true);
            return;
        }
        if (!event.getPlayer().isSneaking() && isRealInteractable(event.getClickedBlock())) {
            event.setUseItemInHand(Result.DENY);
            return;
        }
        event.setCancelled(true);
        Location spawnLocation = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
        if (typeToSpawn == DisplayEntityType.INTERACTION) {
            spawnLocation.add(0.5, 0.0, 0.5);
        } else if (typeToSpawn != DisplayEntityType.BLOCK) {
            spawnLocation.add(0.5, 0.5, 0.5);
        }
        if (plugin.getWorldGuardHelper() != null) {
            if (!plugin.getWorldGuardHelper().canBuild(event.getPlayer(), spawnLocation)) {
                Messages.sendError(event.getPlayer(), "Du hast hier keine Baurechte.");
                return;
            }
        }
        Entity display = null;
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
        } else if (typeToSpawn == DisplayEntityType.INTERACTION) {
            display = spawnLocation.getWorld().spawn(spawnLocation, Interaction.class, e -> {
                e.setInteractionWidth(1.0f);
                e.setInteractionHeight(1.0f);
            });
        } else {
            throw new RuntimeException("Unknown type: " + typeToSpawn);
        }
        if (display != null) {
            YamlConfiguration conf = new YamlConfiguration();
            conf.set("owner", event.getPlayer().getUniqueId().toString());
            display.getPersistentDataContainer().set(plugin.getDataNamespacedKey(), PersistentDataType.STRING, conf.saveToString());
            plugin.setCurrentEditingDisplayEntity(event.getPlayer().getUniqueId(), new DisplayEntityData(plugin, display));
            Messages.sendSuccess(event.getPlayer(), "Das Display-Entity wurde gespawnt und ausgewählt.");
        } else {
            Messages.sendError(event.getPlayer(), "Das Display-Entity konnte nicht gespawnt werden.");
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
        if (!(event.getRightClicked() instanceof ItemFrame)) {
            if (plugin.getDisplayEntityType(event.getPlayer().getInventory().getItem(event.getHand())) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) {
            if (plugin.getDisplayEntityType(event.getPlayer().getInventory().getItem(event.getHand())) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerArmorStandManipulateEvent event) {
        if (plugin.getDisplayEntityType(event.getPlayer().getInventory().getItem(event.getHand())) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.setCurrentEditingDisplayEntity(event.getPlayer().getUniqueId(), null);
    }
}
