package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_SWORD_TRIDENT_AXE;

public class WeaponArt extends Enchantment {

    private static int percentreduction = 0;

    public WeaponArt() {
        super(Rarity.UNCOMMON, IS_SWORD_TRIDENT_AXE,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @SubscribeEvent
    public static void onHit(LivingHurtEvent event) {
        int EnchantLevel;
        DamageSource damageSource = event.getSource();
        LivingEntity victim = event.getEntity();
        LivingEntity attacker;
        try {
            attacker = (LivingEntity) damageSource.getEntity();
        } catch (Exception e) {
            return;
        }
        if (attacker == null || victim == null)
            return;
        try {
            EnchantLevel =
                    EnchantmentHelper.getEnchantmentLevel(RegisterEnch.WEAPON_ART.get(),
                            victim);
        } catch (Exception e) {
            return;
        }
        if (EnchantLevel <= 0)
            return;

        ItemStack mainHandItem = attacker.getMainHandItem();
        ItemStack offHandItem = attacker.getOffhandItem();
        Item mainItem = mainHandItem.getItem();
        Item offItem = offHandItem.getItem();
        if (!(mainItem instanceof AxeItem
                || mainItem instanceof HoeItem
                || mainItem instanceof PickaxeItem
                || mainItem instanceof ShovelItem
                || mainItem instanceof SwordItem
                || mainItem instanceof TridentItem
                || mainItem instanceof BowItem
                || mainItem instanceof CrossbowItem
                || offItem instanceof AxeItem
                || offItem instanceof HoeItem
                || offItem instanceof PickaxeItem
                || offItem instanceof ShovelItem
                || offItem instanceof SwordItem
                || offItem instanceof TridentItem
                || offItem instanceof BowItem
                || offItem instanceof CrossbowItem)) {
            return;
        }
        percentreduction =
                EnchantLevel * Math.max((Integer) getConfig("weaponart",
                        "percentreductionperlvl", 1), 0);
        if (percentreduction > 100) percentreduction = 100;
        event.setAmount(event.getAmount() * (1 - percentreduction * 0.01F));
    }

    public int getMinCost(int enchantmentLevel) {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 30;
    }

    public int getMaxLevel() {
        return Math.max((Integer) getConfig("weaponart", "maxlevel", 1), 1);
    }
}