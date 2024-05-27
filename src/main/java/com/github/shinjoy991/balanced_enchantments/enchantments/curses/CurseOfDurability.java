package com.github.shinjoy991.balanced_enchantments.enchantments.curses;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;

import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_TOOL_AND_WEAPON_AND_ARMOR;

public class CurseOfDurability extends Enchantment {

    public CurseOfDurability() {
        super(Enchantment.Rarity.VERY_RARE, IS_TOOL_AND_WEAPON_AND_ARMOR,
                new EquipmentSlotType[]{EquipmentSlotType.MAINHAND,
                        EquipmentSlotType.OFFHAND,
                        EquipmentSlotType.CHEST,
                        EquipmentSlotType.HEAD,
                        EquipmentSlotType.LEGS,
                        EquipmentSlotType.FEET
                });
    }

    public boolean isCurse() {
        return true;
    }

    public int getMinCost(int enchantmentLevel) {
        return 10;
    }

    public int getMaxCost(int enchantmentLevel) {
        return 30;
    }

    public boolean isTreasureOnly() {
        return true;
    }

}