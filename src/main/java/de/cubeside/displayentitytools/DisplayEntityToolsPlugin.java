package de.cubeside.displayentitytools;

import de.cubeside.displayentitytools.chestshop.ChestShopListener;
import de.cubeside.displayentitytools.commands.GetItemCommand;
import de.cubeside.displayentitytools.commands.ListCommand;
import de.cubeside.displayentitytools.commands.SelectCommand;
import de.cubeside.displayentitytools.commands.edit.AddOrRemoveOwnerCommand;
import de.cubeside.displayentitytools.commands.edit.DeleteTextLineCommand;
import de.cubeside.displayentitytools.commands.edit.GetNbtCommand;
import de.cubeside.displayentitytools.commands.edit.RemoveCommand;
import de.cubeside.displayentitytools.commands.edit.SetBillboardModeCommand;
import de.cubeside.displayentitytools.commands.edit.SetBlockCommand;
import de.cubeside.displayentitytools.commands.edit.SetGlowingCommand;
import de.cubeside.displayentitytools.commands.edit.SetItemCommand;
import de.cubeside.displayentitytools.commands.edit.SetLightLevelCommand;
import de.cubeside.displayentitytools.commands.edit.SetNameCommand;
import de.cubeside.displayentitytools.commands.edit.SetPositionCommand;
import de.cubeside.displayentitytools.commands.edit.SetRotationCommand;
import de.cubeside.displayentitytools.commands.edit.SetSeeTextThroughBlocksCommand;
import de.cubeside.displayentitytools.commands.edit.SetShadowCommand;
import de.cubeside.displayentitytools.commands.edit.SetTextAlignCommand;
import de.cubeside.displayentitytools.commands.edit.SetTextAlphaCommand;
import de.cubeside.displayentitytools.commands.edit.SetTextBackgroundColorCommand;
import de.cubeside.displayentitytools.commands.edit.SetTextCommand;
import de.cubeside.displayentitytools.commands.edit.SetTextLineCommand;
import de.cubeside.displayentitytools.commands.edit.SetTextLineWidthCommand;
import de.cubeside.displayentitytools.commands.edit.SetTextShadowCommand;
import de.cubeside.displayentitytools.commands.edit.SetTransformMatrixCommand;
import de.cubeside.displayentitytools.commands.edit.SetTransformRotationCommand;
import de.cubeside.displayentitytools.commands.edit.SetTransformTranslationScaleCommand;
import de.cubeside.nmsutils.NMSUtils;
import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.iani.cubesideutils.bukkit.items.CustomHeads;
import de.iani.playerUUIDCache.PlayerUUIDCache;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DisplayEntityToolsPlugin extends JavaPlugin {

    public static final String ITEM_DISPLAY_NAME = "Item-Display";
    public static final String BLOCK_DISPLAY_NAME = "Block-Display";
    public static final String TEXT_DISPLAY_NAME = "Text-Display";

    private NamespacedKey spawnerNamespacedKey;
    private NamespacedKey dataNamespacedKey;
    private ItemStack textSpawnerItem;
    private ItemStack blockSpawnerItem;
    private ItemStack itemSpawnerItem;
    private WorldGuardHelper worldGuardHelper;
    private NMSUtils nmsUtils;
    private PlayerUUIDCache playerUUIDCache;

    private HashMap<UUID, UUID> currentEditingDisplayEntity = new HashMap<>();
    private boolean ignoreDisplayEntityOwner;

    public DisplayEntityToolsPlugin() {

    }

    @Override
    public void onEnable() {
        spawnerNamespacedKey = new NamespacedKey(this, "spawner");
        dataNamespacedKey = new NamespacedKey(this, "data");
        createSpawnerItems();
        configureCommands();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        Plugin worldGuardPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuardPlugin != null) {
            worldGuardHelper = new WorldGuardHelper(worldGuardPlugin);
        }
        Plugin playerUUIDCache = getServer().getPluginManager().getPlugin("PlayerUUIDCache");
        if (playerUUIDCache != null) {
            this.playerUUIDCache = (PlayerUUIDCache) playerUUIDCache;
        }
        if (getServer().getPluginManager().getPlugin("CubesideNMSUtils") != null) {
            nmsUtils = getServer().getServicesManager().load(NMSUtils.class);
        }

        if (getServer().getPluginManager().getPlugin("ChestShop") != null) {
            this.getServer().getPluginManager().registerEvents(new ChestShopListener(this), this);
        }

        saveDefaultConfig();
        ignoreDisplayEntityOwner = getConfig().getBoolean("ignoreDisplayEntityOwner");
    }

    private void configureCommands() {
        CommandRouter displayentityCommands = new CommandRouter(getCommand("displayentity"));
        displayentityCommands.addCommandMapping(new GetItemCommand(this), "getitem");
        displayentityCommands.addCommandMapping(new ListCommand(this), "list");
        displayentityCommands.addCommandMapping(new SelectCommand(this), "select");

        displayentityCommands.addCommandMapping(new AddOrRemoveOwnerCommand(this, true), "addowner");
        displayentityCommands.addCommandMapping(new AddOrRemoveOwnerCommand(this, false), "removeowner");

        displayentityCommands.addCommandMapping(new RemoveCommand(this), "remove");
        displayentityCommands.addCommandMapping(new SetNameCommand(this), "setname");
        displayentityCommands.addCommandMapping(new SetPositionCommand(this), "setposition");
        displayentityCommands.addCommandMapping(new SetRotationCommand(this), "setrotation");
        displayentityCommands.addCommandMapping(new SetBillboardModeCommand(this), "setbillboardmode");
        displayentityCommands.addCommandMapping(new SetTransformRotationCommand(this, true), "setleftrotation");
        displayentityCommands.addCommandMapping(new SetTransformRotationCommand(this, false), "setrightrotation");
        displayentityCommands.addCommandMapping(new SetTransformTranslationScaleCommand(this, true), "setscale");
        displayentityCommands.addCommandMapping(new SetTransformTranslationScaleCommand(this, false), "settranslation");
        displayentityCommands.addCommandMapping(new SetTransformMatrixCommand(this), "settransformmatrix");
        displayentityCommands.addCommandMapping(new SetLightLevelCommand(this), "setlightlevel");
        displayentityCommands.addCommandMapping(new SetShadowCommand(this), "setshadow");
        displayentityCommands.addCommandMapping(new SetGlowingCommand(this), "setglowing");
        displayentityCommands.addCommandMapping(new GetNbtCommand(this), "getnbt");

        displayentityCommands.addCommandMapping(new SetTextCommand(this, SetTextCommand.Mode.SET), "text", "set");
        displayentityCommands.addCommandMapping(new SetTextCommand(this, SetTextCommand.Mode.ADD), "text", "add");
        displayentityCommands.addCommandMapping(new SetTextCommand(this, SetTextCommand.Mode.ADDLINE), "text", "addline");
        displayentityCommands.addCommandMapping(new SetTextLineCommand(this, SetTextLineCommand.Mode.SET), "text", "setline");
        displayentityCommands.addCommandMapping(new SetTextLineCommand(this, SetTextLineCommand.Mode.INSERT), "text", "insertline");
        displayentityCommands.addCommandMapping(new DeleteTextLineCommand(this), "text", "deleteline");
        displayentityCommands.addCommandMapping(new SetTextAlignCommand(this), "settextalign");
        displayentityCommands.addCommandMapping(new SetSeeTextThroughBlocksCommand(this), "setseetextthroughblocks");
        displayentityCommands.addCommandMapping(new SetTextShadowCommand(this), "settextshadow");
        displayentityCommands.addCommandMapping(new SetTextAlphaCommand(this), "settextalpha");
        displayentityCommands.addCommandMapping(new SetTextBackgroundColorCommand(this), "settextbackgroundcolor");
        displayentityCommands.addCommandMapping(new SetTextLineWidthCommand(this), "settextlinewidth");

        displayentityCommands.addCommandMapping(new SetItemCommand(this), "setitem");
        displayentityCommands.addCommandMapping(new SetBlockCommand(this), "setblock");
    }

    private void createSpawnerItems() {
        textSpawnerItem = CustomHeads.createHead(UUID.fromString("98ee1b6a-7f3d-4fed-a09c-a5bdbc76e8ea"), "DETTextSpawner",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE5YmRlZjE1MTgzYzNkMGRhYzcyZWVlNzIxNGIxM2ZkYWU0ZTM4OTYwOWExMjEyZDFlMTYzMmEzZDg1YTMxIn19fQ==");
        ItemMeta meta = textSpawnerItem.getItemMeta();
        meta.getPersistentDataContainer().set(spawnerNamespacedKey, PersistentDataType.STRING, DisplayEntityType.TEXT.name());
        meta.displayName(Component.text(TEXT_DISPLAY_NAME).color(NamedTextColor.LIGHT_PURPLE));
        meta.lore(List.of(
                Component.text("Spawnt ein ").color(NamedTextColor.WHITE)
                        .append(Component.text("Text-Display-Entity").color(NamedTextColor.LIGHT_PURPLE)),
                Component.text("an dem ausgewählten Ort.").color(NamedTextColor.WHITE)));
        textSpawnerItem.setItemMeta(meta);

        blockSpawnerItem = CustomHeads.createHead(UUID.fromString("de2038fc-b032-4722-8736-5c33b4fa36be"), "DETBlockSpawner",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU4ZjJmZTM0ZDk3Nzk2YzE3YzAyZDJhNDM2YjA2YzE5MWFkNjY4MDVhNmY3NTU4YjIxOWFkM2Q4NGYzYzhiNCJ9fX0=");
        meta = blockSpawnerItem.getItemMeta();
        meta.getPersistentDataContainer().set(spawnerNamespacedKey, PersistentDataType.STRING, DisplayEntityType.BLOCK.name());
        meta.displayName(Component.text(BLOCK_DISPLAY_NAME).color(NamedTextColor.LIGHT_PURPLE));
        meta.lore(List.of(
                Component.text("Spawnt ein ").color(NamedTextColor.WHITE)
                        .append(Component.text("Block-Display-Entity").color(NamedTextColor.LIGHT_PURPLE)),
                Component.text("an dem ausgewählten Ort.").color(NamedTextColor.WHITE)));
        blockSpawnerItem.setItemMeta(meta);

        itemSpawnerItem = CustomHeads.createHead(UUID.fromString("2759a198-5afa-467b-8210-0525a2d937fa"), "DETItemSpawner",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ4YTg5MWUxNTFmZWI2N2Y0YjYyMGFlODVjZWRiN2Q5M2YxNjhjNTQxNzc3ZTBkNmNjMWViZDdhY2M0ODU3OSJ9fX0=");
        meta = itemSpawnerItem.getItemMeta();
        meta.getPersistentDataContainer().set(spawnerNamespacedKey, PersistentDataType.STRING, DisplayEntityType.ITEM.name());
        meta.displayName(Component.text(ITEM_DISPLAY_NAME).color(NamedTextColor.LIGHT_PURPLE));
        meta.lore(List.of(
                Component.text("Spawnt ein ").color(NamedTextColor.WHITE)
                        .append(Component.text("Item-Display-Entity").color(NamedTextColor.LIGHT_PURPLE)),
                Component.text("an dem ausgewählten Ort.").color(NamedTextColor.WHITE)));
        itemSpawnerItem.setItemMeta(meta);
    }

    public ItemStack getSpawnerItem(DisplayEntityType type) {
        ItemStack stack = switch (type) {
            case TEXT -> textSpawnerItem;
            case BLOCK -> blockSpawnerItem;
            case ITEM -> itemSpawnerItem;
            default -> throw new IllegalArgumentException("Not implemented type: " + type);
        };
        return new ItemStack(stack);
    }

    public String getDisplayName(DisplayEntityType type) {
        return switch (type) {
            case TEXT -> TEXT_DISPLAY_NAME;
            case BLOCK -> BLOCK_DISPLAY_NAME;
            case ITEM -> ITEM_DISPLAY_NAME;
            default -> throw new IllegalArgumentException("Not implemented type: " + type);
        };
    }

    public DisplayEntityType getDisplayEntityType(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return null;
        }
        String typeString = stack.getItemMeta().getPersistentDataContainer().get(spawnerNamespacedKey, PersistentDataType.STRING);
        if (typeString != null) {
            try {
                return DisplayEntityType.valueOf(typeString);
            } catch (IllegalArgumentException e) {
                getLogger().log(Level.SEVERE, "Invalid DisplayEntityType in ItemStack " + stack + ": " + typeString, e);
            }
        }
        return null;
    }

    public UUID getCurrentEditingDisplayEntity(UUID player) {
        return currentEditingDisplayEntity.get(player);
    }

    public void setCurrentEditingDisplayEntity(UUID player, UUID editing) {
        if (editing == null) {
            currentEditingDisplayEntity.remove(player);
        } else {
            currentEditingDisplayEntity.put(player, editing);
        }
    }

    public NamespacedKey getDataNamespacedKey() {
        return dataNamespacedKey;
    }

    public WorldGuardHelper getWorldGuardHelper() {
        return worldGuardHelper;
    }

    public PlayerUUIDCache getPlayerUUIDCache() {
        return playerUUIDCache;
    }

    public NMSUtils getNmsUtils() {
        return nmsUtils;
    }

    public boolean isIgnoreDisplayEntityOwner() {
        return ignoreDisplayEntityOwner;
    }

    public boolean canEdit(Player player, DisplayEntityData displayEntity) {
        if (!ignoreDisplayEntityOwner) {
            if (!displayEntity.getOwner().contains(player.getUniqueId())
                    && !player.hasPermission(DisplayEntityToolsPermissions.PERMISSION_EDIT_ALL)) {
                return false;
            }
        }
        if (worldGuardHelper != null && !worldGuardHelper.canBuild(player, displayEntity.getLocation())) {
            return false;
        }
        return true;
    }
}
