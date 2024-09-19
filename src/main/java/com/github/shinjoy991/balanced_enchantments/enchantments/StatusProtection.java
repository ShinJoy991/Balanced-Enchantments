package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import net.minecraft.server.gui.PlayerListComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.helpers.DelayFunc.delayedTask;

public class StatusProtection extends Enchantment {

    private static final Set<UUID> cooldowns = new HashSet<>();
    private static final List<Effect> originalEffects = Arrays.asList(
            Effects.WITHER,
            Effects.POISON,
            Effects.HUNGER,
            Effects.BLINDNESS,
            Effects.MOVEMENT_SLOWDOWN,
            Effects.DIG_SLOWDOWN,
            Effects.CONFUSION
    );
    private static final List<Effect> additionalEffects = Arrays.asList(
            Effects.WITHER,
            Effects.POISON,
            Effects.HUNGER,
            Effects.BLINDNESS,
            Effects.MOVEMENT_SLOWDOWN,
            Effects.DIG_SLOWDOWN,
            Effects.WEAKNESS,
            Effects.CONFUSION
    );
    public static int STATUS_COOLDOWN_TICKS = 5;
    public static int STATUS_COOLDOWN_REDUCE_PER_LEVEL = 0;
    public static int STATUS_CHANCE = 0;
    public static int STATUS_CHANCE_INCREASE_PER_LEVEL = 0;
    public static int STATUS_ALL_THRESHOLD = 0;

    public StatusProtection() {
        super(Rarity.RARE, EnchantmentType.ARMOR,
                new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST,
                        EquipmentSlotType.LEGS,
                        EquipmentSlotType.FEET});
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        cooldowns.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
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
    }

    private static boolean hasStatusProtection(ServerPlayerEntity player) {
        return EnchantmentHelper.getEnchantmentLevel(RegisterEnch.STATUS_PROTECTION.get(),
                player) > 0;
    }

    private static int getStatusProtection(ServerPlayerEntity player) {
        int totalProtectionLevel = 0;
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getType() == EquipmentSlotType.Group.ARMOR) {
                ItemStack itemStack = player.getItemBySlot(slot);
                totalProtectionLevel += EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.STATUS_PROTECTION.get(), itemStack);
            }
        }
        return totalProtectionLevel;
    }

    private static void removeNegativeEffect(ServerPlayerEntity player, int statusProtectionLevel) {
        List<Effect> effectsToConsider;
        if (statusProtectionLevel >= STATUS_ALL_THRESHOLD) {
            effectsToConsider = additionalEffects;
            try {
                for (Effect effect : ForgeRegistries.POTIONS) {
                    if (effect.getCategory() == EffectType.HARMFUL) {
                        effectsToConsider.add(effect);
                    }
                }
            } catch (Exception ignored) {}
        } else {
            effectsToConsider = originalEffects;
        }
        Collections.shuffle(effectsToConsider);
        for (Effect effect : effectsToConsider) {
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