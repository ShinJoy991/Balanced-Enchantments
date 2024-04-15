package com.github.shinjoy991.balancedenchantments.enchantments;

import com.github.shinjoy991.balancedenchantments.register.RegisterEnch;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.shinjoy991.balancedenchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balancedenchantments.helpers.DelayFunc.delayedTask;
import static com.github.shinjoy991.balancedenchantments.register.RegisterEnch.IS_TOOL_AND_WEAPON;

public class SuperCharged extends Enchantment {

    private static final Map<UUID, Boolean> cooldowns = new HashMap<>();
    private static int adddmg = 0;

    public SuperCharged() {
        super(Rarity.UNCOMMON, IS_TOOL_AND_WEAPON,
                new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    @SubscribeEvent
    public static void onHit(LivingHurtEvent event) {
        int EnchantLevel;
        DamageSource damageSource = event.getSource();
        LivingEntity target = event.getEntityLiving();
        LivingEntity attacker = (LivingEntity) damageSource.getEntity();
        if (attacker == null || target == null)
            return;
        try {
            EnchantLevel =
                    EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.SUPER_CHARGED.get(),
                            attacker.getMainHandItem());
        } catch (Exception e) {
            return;
        }
        if (EnchantLevel <= 0)
            return;
        if (cooldowns.getOrDefault(attacker.getUUID(), false))
            return;
        adddmg =
                EnchantLevel * Math.max((Integer) getConfig("supercharged", "dmgaddperlvl", 1), 0);
        event.setAmount(event.getAmount() + adddmg);
    }

    public int getMinCost(int enchantmentLevel) {
        return 10 + 15 * (enchantmentLevel - 1);
    }

    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 30;
    }

    public int getMaxLevel() {
        return 4;
    }

    public void doPostAttack(LivingEntity user, Entity target, int level) {
        if (!(target instanceof LivingEntity))
            return;
        try {
            if (EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.SUPER_CHARGED.get(),
                            user.getMainHandItem()) <= 0)
                return;
        } catch (Exception e) {
            return;
        }
        if (cooldowns.getOrDefault(user.getUUID(), false))
            return;
        int weaknessduration = Math.max((Integer) getConfig("supercharged",
                "weaknessduration", 1), 0);
        int weaknesslvl = Math.max((Integer) getConfig("supercharged",
                "weaknesslvl", 1), 0);
        if (weaknessduration > 0)
            user.addEffect(new EffectInstance(Effects.WEAKNESS, weaknessduration, weaknesslvl));
        cooldowns.put(user.getUUID(), true);
        int cooldown = Math.max((Integer) getConfig("supercharged",
                "cooldown", 1), 0);
        delayedTask(cooldown, () -> cooldowns.remove(user.getUUID()));
    }

    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && !(ench instanceof DamageEnchantment);
    }
}