package de.cubeside.displayentitytools;

import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

public enum DisplayEntityType {
    TEXT("Text-Display-Entity") {
        @Override
        public Class<? extends Entity> getEntityClass() {
            return TextDisplay.class;
        }
    },
    BLOCK("Block-Display-Entity") {
        @Override
        public Class<? extends Entity> getEntityClass() {
            return BlockDisplay.class;
        }
    },
    ITEM("Item-Display-Entity") {
        @Override
        public Class<? extends Entity> getEntityClass() {
            return ItemDisplay.class;
        }
    },
    INTERACTION("Interaction-Entity") {
        @Override
        public Class<? extends Entity> getEntityClass() {
            return Interaction.class;
        }
    };

    private final String displayName;

    abstract public Class<? extends Entity> getEntityClass();

    DisplayEntityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DisplayEntityType getByClass(Class<? extends Entity> clazz) {
        if (TextDisplay.class.isAssignableFrom(clazz)) {
            return TEXT;
        }
        if (BlockDisplay.class.isAssignableFrom(clazz)) {
            return BLOCK;
        }
        if (ItemDisplay.class.isAssignableFrom(clazz)) {
            return ITEM;
        }
        if (Interaction.class.isAssignableFrom(clazz)) {
            return INTERACTION;
        }
        throw new IllegalArgumentException("Unknown Display Class: " + clazz);
    }
}
