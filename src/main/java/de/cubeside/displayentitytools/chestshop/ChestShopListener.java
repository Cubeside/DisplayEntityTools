package de.cubeside.displayentitytools.chestshop;

import com.Acrobot.ChestShop.Events.PreShopCreationItemDisplayNameEvent;
import de.cubeside.displayentitytools.DisplayEntityToolsPlugin;
import de.cubeside.displayentitytools.DisplayEntityType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ChestShopListener implements Listener {
    private final DisplayEntityToolsPlugin displayEntityToolsPlugin;
    private final DisplayEntityToolsPriorityItemDisplayNameShortener displayEntityToolsPriorityItemDisplayNameShortener;

    public ChestShopListener(DisplayEntityToolsPlugin displayEntityToolsPlugin) {
        this.displayEntityToolsPlugin = displayEntityToolsPlugin;
        this.displayEntityToolsPriorityItemDisplayNameShortener = new DisplayEntityToolsPriorityItemDisplayNameShortener();
    }

    @EventHandler
    public void onShopDisplayNaming(PreShopCreationItemDisplayNameEvent preShopCreationItemDisplayNameEvent) {

        ItemStack itemStack = preShopCreationItemDisplayNameEvent.getItemStack();

        DisplayEntityType displayEntityType = displayEntityToolsPlugin.getDisplayEntityType(itemStack);
        if (displayEntityType == null)
            return;

        String itemName = displayEntityToolsPlugin.getDisplayName(displayEntityType);
        preShopCreationItemDisplayNameEvent.setDisplayName(ChatColor.stripColor(itemName),
                displayEntityToolsPriorityItemDisplayNameShortener);
    }
}
