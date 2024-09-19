package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_BOOTS;
import static net.minecraft.enchantment.Enchantments.FALL_PROTECTION;

public class HeroLanding extends Enchantment {

    public HeroLanding() {
        super(Rarity.UNCOMMON, IS_BOOTS,
                new EquipmentSlotType[]{EquipmentSlotType.FEET});
    }

    @SubscribeEvent
    public static void onHit(LivingHurtEvent event) {
        int EnchantLevel;
        DamageSource damageSource = event.getSource();
        if (!damageSource.equals(DamageSource.FALL)) {
            return;
        }
        LivingEntity self = event.getEntityLiving();
        try {
            EnchantLevel = EnchantmentHelper.getEnchantmentLevel(RegisterEnch.HERO_LANDING.get(),
                    self);
        } catch (Exception e) {
            return;
        }
        int height =
                EnchantLevel * Math.max((Integer) getConfig("herolanding", "heightaddperlvl", 1),
                        0) + 2;
        if (EnchantLevel >= 1 && self.fallDistance <= height) {
            event.setAmount(0);
        }
    }

    public int getMinCost(int enchantmentLevel) {
        return 8;
    }

    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 50;
    }

    public int getMaxLevel() {
        return Math.max((Integer) getConfig("herolanding", "maxlevel", 1), 1);
    }

    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && ench != FALL_PROTECTION;
    }
}