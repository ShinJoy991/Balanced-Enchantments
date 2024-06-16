package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.helpers.DelayFunc.delayedTask;

public class HuntingInstinct extends Enchantment {

    private static final Set<UUID> cooldowns = new HashSet<>();

    public HuntingInstinct() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (ServerLevel world : event.getServer().getAllLevels()) {
                for (Entity entity : world.getAllEntities()) {
                    if (entity instanceof LivingEntity) {
                        UUID entityId = entity.getUUID();
                        if (!cooldowns.contains(entityId) && hasHuntingInstinct((LivingEntity) entity)) {
                            int slownesslvl = Math.max((Integer) getConfig("huntinginstinct",
                                    "slownesslvl", 1), 0);
                            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200,
                                slownesslvl, true, false));
                            cooldowns.add(entityId);
                            delayedTask(198, () -> cooldowns.remove(entityId));
                        }
                    }
                }
            }
        }
    }

    private static boolean hasHuntingInstinct(LivingEntity player) {
        return EnchantmentHelper.getEnchantmentLevel(RegisterEnch.HUNTING_INSTINCT.get(),
                player) > 0;
    }

    public void doPostAttack(LivingEntity user, Entity target, int level) {

        if (Math.max((Integer) getConfig("huntinginstinct", "chance", 1), 0) <= new Random().nextInt(100) + 1)
            return;
        try {
            if (!(EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.HUNTING_INSTINCT.get(),
                    user.getMainHandItem()) > 0))
                return;
        } catch (Exception e) {
            return;
        }
        if (!(target instanceof LivingEntity))
            return;
        Level targetworld = target.level();
        if (targetworld.getServer() == null)
            return;
        int strengthlvl = Math.max((Integer) getConfig("huntinginstinct",
                "strengthlvl", 1), 0);
        int speedlvl = Math.max((Integer) getConfig("huntinginstinct",
                "speedlvl", 1), 0);
        user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, strengthlvl, true, false));
        user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, speedlvl, true, false));
        user.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
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

    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && ench != RegisterEnch.SUPER_CHARGED.get();
    }
}