package com.github.shinjoy991.balanced_enchantments.enchantments.curses;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_TOOL_AND_WEAPON_AND_ARMOR;

public class CurseOfDurability extends Enchantment {

    public CurseOfDurability() {
        super(Rarity.VERY_RARE, IS_TOOL_AND_WEAPON_AND_ARMOR,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND,
                        EquipmentSlot.OFFHAND,
                        EquipmentSlot.CHEST,
                        EquipmentSlot.HEAD,
                        EquipmentSlot.LEGS,
                        EquipmentSlot.FEET
                });
    }

    public boolean isCurse() {
        return true;
    }

    public int getMinCost(int enchantmentLevel) {
        return 30;
    }

    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }

    public boolean isTreasureOnly() {
        return true;
    }

    public boolean isTradeable() {
        return false;
    }
}