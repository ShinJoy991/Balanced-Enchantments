package com.github.shinjoy991.balanced_enchantments.enchantments;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_SWORD_TRIDENT_AXE;

public class TrueSharpness extends Enchantment {

    private static int adddmg = 0;

    public TrueSharpness() {
        super(Rarity.VERY_RARE, IS_SWORD_TRIDENT_AXE,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public float getDamageBonus(int level, MobType creatureType, ItemStack stack) {
        adddmg =
                Math.max((Integer) getConfig("truesharpness", "adddmg", 1), 0);
        return adddmg;
    }

    public int getMinCost(int p_45000_) {
        return 30;
    }

    public int getMaxCost(int p_45002_) {
        return super.getMinCost(p_45002_) + 50;
    }

    public boolean isAllowedOnBooks() {
        return false;
    }

    public boolean isTreasureOnly() {
        return true;
    }

    public boolean isTradeable() {
        return false;
    }

    @Override
    public Component getFullname(int level) {
        return new TranslatableComponent(this.getDescriptionId()).withStyle(style -> style.withColor(TextColor.fromRgb(0xFFA500)));
    }

    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && !(ench instanceof DamageEnchantment);
    }
}