package de.cubeside.displayentitytools.chestshop;

import com.Acrobot.ChestShop.ItemNaming.PriorityItemDisplayNameShortener;

public class DisplayEntityToolsPriorityItemDisplayNameShortener extends PriorityItemDisplayNameShortener {

    public DisplayEntityToolsPriorityItemDisplayNameShortener() {

        addMapping(0, "Block-Display", "Block Display");
        addMapping(0, "Text-Display", "Text Display");
        addMapping(0, "Item-Display", "Item Display");

        addMapping(10, "Display", "Disp");
    }
}
