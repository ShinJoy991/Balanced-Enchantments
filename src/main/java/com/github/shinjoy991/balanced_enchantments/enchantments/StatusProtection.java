package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.helpers.DelayFunc.delayedTask;

public class StatusProtection extends Enchantment {

    private static final Set<UUID> cooldowns = new HashSet<>();
    private static final List<MobEffect> originalEffects = Arrays.asList(
            MobEffects.WITHER,
            MobEffects.POISON,
            MobEffects.HUNGER,
            MobEffects.BLINDNESS
    );
    private static final List<MobEffect> additionalEffects = Arrays.asList(
            MobEffects.WITHER,
            MobEffects.POISON,
            MobEffects.HUNGER,
            MobEffects.BLINDNESS,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.DIG_SLOWDOWN,
            MobEffects.WEAKNESS,
            MobEffects.DARKNESS,
            MobEffects.CONFUSION
    );
    public static int STATUS_COOLDOWN_TICKS = 5;
    public static int STATUS_COOLDOWN_REDUCE_PER_LEVEL = 0;
    public static int STATUS_CHANCE = 0;
    public static int STATUS_CHANCE_INCREASE_PER_LEVEL = 0;
    public static int STATUS_ALL_THRESHOLD = 0;

    public StatusProtection() {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR,
                new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
                        EquipmentSlot.FEET});
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                UUID playerId = player.getUUID();

                if (!cooldowns.contains(playerId) && hasStatusProtection(player)) {
                    removeNegativeEffect(player, getStatusProtection(player));
                    cooldowns.add(playerId);
                    int lastcd =
                            STATUS_COOLDOWN_TICKS - (getStatusProtection(player) - 1) * STATUS_COOLDOWN_REDUCE_PER_LEVEL;
                    if (lastcd <= 0)
                        lastcd = 5;
                    delayedTask(lastcd, () -> cooldowns.remove(playerId));
                }
            }
        }
    }

    private static boolean hasStatusProtection(ServerPlayer player) {
        return EnchantmentHelper.getEnchantmentLevel(RegisterEnch.STATUS_PROTECTION.get(),
                player) > 0;
    }

    private static int getStatusProtection(ServerPlayer player) {
        int totalProtectionLevel = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack itemStack = player.getItemBySlot(slot);
                totalProtectionLevel += EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.STATUS_PROTECTION.get(), itemStack);
            }
        }
        return totalProtectionLevel;
    }

    private static void removeNegativeEffect(ServerPlayer player, int statusProtectionLevel) {
        List<MobEffect> effectsToConsider;
        if (statusProtectionLevel >= STATUS_ALL_THRESHOLD) {
            effectsToConsider = additionalEffects;
        } else {
            effectsToConsider = originalEffects;
        }
        Collections.shuffle(effectsToConsider);
        for (MobEffect effect : effectsToConsider) {
            if (player.hasEffect(effect)) {
                int chance =
                        STATUS_CHANCE + (statusProtectionLevel - 1) * STATUS_CHANCE_INCREASE_PER_LEVEL;
                if (Math.random() * 100 < chance) {
                    player.removeEffect(effect);
                    player.playNotifySound(SoundEvents.ENCHANTMENT_TABLE_USE, player.getSoundSource(),1,1);
                    break;
                }
            }
        }
    }

    public int getMinCost(int p_45083_) {
        return 10 + (p_45083_ - 1) * 8;
    }

    public int getMaxCost(int p_45085_) {
        return super.getMinCost(p_45085_) + 50;
    }

    public int getMaxLevel() {
        return Math.max((Integer) getConfig("statusprotection", "maxlevel", 1), 1);
    }
}