package com.github.shinjoy991.balanced_enchantments.enchantments;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_SWORD_TRIDENT_AXE;

public class TrueSharpness extends Enchantment {

    private static int adddmg = 0;

    public TrueSharpness() {
        super(Rarity.VERY_RARE, IS_SWORD_TRIDENT_AXE,
                new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    @Override
    public float getDamageBonus(int level, CreatureAttribute creatureType) {
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
    public ITextComponent getFullname(int level) {
        return new TranslationTextComponent(this.getDescriptionId()).withStyle(TextFormatting.GOLD);
    }

    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && !(ench instanceof DamageEnchantment);
    }
}