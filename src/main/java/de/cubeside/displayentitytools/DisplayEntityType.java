package de.cubeside.displayentitytools;

import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

public enum DisplayEntityType {
    TEXT {
        @Override
        public Class<? extends Display> getEntityClass() {
            return TextDisplay.class;
        }
    },
    BLOCK {
        @Override
        public Class<? extends Display> getEntityClass() {
            return BlockDisplay.class;
        }
    },
    ITEM {
        @Override
        public Class<? extends Display> getEntityClass() {
            return ItemDisplay.class;
        }
    };

    abstract public Class<? extends Display> getEntityClass();

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
