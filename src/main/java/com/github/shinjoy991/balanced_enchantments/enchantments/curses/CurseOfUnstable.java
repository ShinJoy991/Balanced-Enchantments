package com.github.shinjoy991.balanced_enchantments.enchantments.curses;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import java.util.Random;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_TOOL_AND_WEAPON;

public class CurseOfUnstable extends Enchantment {

    public CurseOfUnstable() {
        super(Rarity.VERY_RARE, IS_TOOL_AND_WEAPON,
                new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
    }

    public void doPostHurt(LivingEntity victim, Entity attacker, int p_44694_) {
        if (attacker instanceof LivingEntity) {
            dropItemWithChance(victim, EquipmentSlotType.MAINHAND);
            dropItemWithChance(victim, EquipmentSlotType.OFFHAND);
        }
    }

    private void dropItemWithChance(LivingEntity victim, EquipmentSlotType slot) {
        if (Math.max((Integer) getConfig("curseofunstable", "chance", 1), 0) <= new Random().nextInt(100) + 1)
            return;
        ItemStack itemStack = victim.getItemBySlot(slot);
        if (itemStack.isEmpty() || !itemStack.isEnchanted()) {
            return;
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.CURSE_OF_UNSTABLE.get(),
                itemStack) > 0) {
            ItemStack singleItemStack = itemStack.copy();
            singleItemStack.setCount(1);
            victim.spawnAtLocation(singleItemStack);
            itemStack.shrink(1);
        }
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