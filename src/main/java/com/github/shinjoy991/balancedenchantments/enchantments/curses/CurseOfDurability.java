package com.github.shinjoy991.balancedenchantments.enchantments.curses;

import com.github.shinjoy991.balancedenchantments.register.RegisterEnch;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Random;

import static com.github.shinjoy991.balancedenchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balancedenchantments.register.RegisterEnch.IS_TOOL_AND_WEAPON_AND_ARMOR;

public class CurseOfDurability extends Enchantment {

    public CurseOfDurability() {
        super(Rarity.VERY_RARE, IS_TOOL_AND_WEAPON_AND_ARMOR,
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

    public void doPostAttack(LivingEntity user, Entity target, int p_151368_3_) {
        try {
            if (((PlayerEntity) user).abilities.instabuild) return;
        } catch (Exception ignored) {}
        ItemStack usedhand;
        if (EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.CURSE_OF_DURABILITY.get(),
                            user.getMainHandItem()) > 0) {
            usedhand = user.getMainHandItem();
        }
        else if (EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.CURSE_OF_DURABILITY.get(),
                user.getOffhandItem()) > 0) {
            usedhand = user.getOffhandItem();
        }
        else return;
        System.out.println(usedhand);
        if (usedhand.isDamageableItem()) {
            int minrandom = (Integer) getConfig("curseofdurability", "minrandom", 1);
            int maxrandom = (Integer) getConfig("curseofdurability", "maxrandom", 1);
            int random = new Random().nextInt(maxrandom - minrandom + 1) + minrandom;
            int damage = usedhand.getDamageValue();
            usedhand.setDamageValue(damage + random);
        }
    }

    public void doPostHurt(LivingEntity user, Entity target, int p_151367_3_) {
        Iterable<ItemStack> iterable = user.getArmorSlots();

        for (ItemStack armor : iterable) {
            if (armor.isDamageableItem() && EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.CURSE_OF_DURABILITY.get(),
                    armor) == 1 ) {
                int minrandom = (Integer) getConfig("curseofdurability", "minrandom", 1);
                int maxrandom = (Integer) getConfig("curseofdurability", "maxrandom", 1);
                int random = new Random().nextInt(maxrandom - minrandom + 1) + minrandom;
                int damage = armor.getDamageValue();
                armor.setDamageValue(damage + random);
            }
        }
    }
}