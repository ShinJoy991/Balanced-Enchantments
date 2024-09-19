package com.github.shinjoy991.balanced_enchantments.enchantments.curses;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Objects;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;

public class CurseOfProvocation extends Enchantment {

    public CurseOfProvocation() {
        super(Rarity.VERY_RARE, EnchantmentType.ARMOR,
                new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS,
                        EquipmentSlotType.FEET});
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayerEntity))
            return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.player;
        if (player.abilities.instabuild)
            return;
        int CHECK_INTERVAL = 40;
        if (player.tickCount % CHECK_INTERVAL != 0)
            return;
        if (EnchantmentHelper.getEnchantmentLevel(RegisterEnch.CURSE_OF_PROVOCATION.get(),
                player) > 0) {
            provokeNearbyMobs(player);
        }
    }

    private static void provokeNearbyMobs(ServerPlayerEntity player) {
        int radius = Math.max((Integer) getConfig("curseofprovocation",
                "radius", 1), 0);
        List<Entity> nearbyEntities = player.level.getEntities(player,
                player.getBoundingBox().inflate(radius),
                entity -> entity instanceof MobEntity && canFightBack((MobEntity) entity));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof MobEntity) {
                MobEntity mob = (MobEntity) entity;
                mob.setLastHurtByPlayer(player);
                mob.setTarget(player);
            }
        }
    }

    private static boolean canFightBack(MobEntity  mob) {
        Attribute attackDamage =
                Objects.requireNonNull(mob.getAttribute(Attributes.ATTACK_DAMAGE)).getAttribute();
        return attackDamage.getDefaultValue() > 0;

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