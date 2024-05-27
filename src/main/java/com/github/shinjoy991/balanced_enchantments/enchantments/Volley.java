package com.github.shinjoy991.balanced_enchantments.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_BOW;

public class Volley extends Enchantment {

    public Volley() {

        super(Rarity.RARE, IS_BOW,
                new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    public int getMinCost(int enchantmentLevel) {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 30;
    }

    public int getMaxLevel() {
        return Math.max((Integer) getConfig("volley", "maxlevel", 1), 1);
    }

    @Override
    public boolean checkCompatibility(Enchantment ench) {
        return ench != Enchantments.INFINITY_ARROWS ||
                !getConfig("volley", "matchinfinity", 0).toString().equalsIgnoreCase("false");
    }
}