package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_BOOTS;
import static net.minecraft.world.item.enchantment.Enchantments.FALL_PROTECTION;

public class HeroLanding extends Enchantment {

    public HeroLanding() {
        super(Rarity.UNCOMMON, IS_BOOTS,
                new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @SubscribeEvent
    public static void onHit(LivingHurtEvent event) {
        int EnchantLevel;
        DamageSource damageSource = event.getSource();
        if (!damageSource.is(DamageTypeTags.IS_FALL)) {
            return;
        }
        LivingEntity self = event.getEntity();
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