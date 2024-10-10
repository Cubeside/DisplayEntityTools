package de.cubeside.displayentitytools;

import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

public enum DisplayEntityType {
    TEXT("Text-Display-Entity") {
        @Override
        public Class<? extends Display> getEntityClass() {
            return TextDisplay.class;
        }
    },
    BLOCK("Block-Display-Entity") {
        @Override
        public Class<? extends Display> getEntityClass() {
            return BlockDisplay.class;
        }
    },
    ITEM("Item-Display-Entity") {
        @Override
        public Class<? extends Display> getEntityClass() {
            return ItemDisplay.class;
        }
    };

    private final String displayName;

    abstract public Class<? extends Display> getEntityClass();

    DisplayEntityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DisplayEntityType getByClass(Class<? extends Display> clazz) {
        if (TextDisplay.class.isAssignableFrom(clazz)) {
            return TEXT;
        }
        if (BlockDisplay.class.isAssignableFrom(clazz)) {
            return BLOCK;
        }
        if (ItemDisplay.class.isAssignableFrom(clazz)) {
            return ITEM;
        }
        throw new IllegalArgumentException("Unknown Display Class: " + clazz);
    }
}
