package com.github.shinjoy991.balanced_enchantments.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.KnockbackEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_TOOL_AND_WEAPON;

public class CMoon extends Enchantment {

    public CMoon() {
        super(Rarity.UNCOMMON, IS_TOOL_AND_WEAPON,
                new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity victim, int level) {
        if (level > 0 && victim instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) victim;
            int effecthealth = Math.max((Integer) getConfig("cmoon", "affecthealthbelow", 1), 0);
            if (effecthealth >= target.getMaxHealth()) {
                target.setOnGround(false);
                double power = Math.max((Integer) getConfig("cmoon", "power", 1),
                        0) * 0.1D * level * (1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                if (power != 0) {
                    target.push(0, power, 0);
                }
            }
        }
    }

    public int getMinCost(int p_45083_) {
        return 5 + 20 * (p_45083_ - 1);
    }

    public int getMaxCost(int p_45085_) {
        return super.getMinCost(p_45085_) + 50;
    }

    public int getMaxLevel() {
        return Math.max((Integer) getConfig("cmoon", "maxlevel", 1), 1);
    }

    @Override
    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && !(ench instanceof KnockbackEnchantment);
    }
}