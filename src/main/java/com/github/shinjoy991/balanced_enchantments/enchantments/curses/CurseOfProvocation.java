package com.github.shinjoy991.balanced_enchantments.enchantments.curses;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Objects;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;

public class CurseOfProvocation extends Enchantment {

    public CurseOfProvocation() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR,
                new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
                        EquipmentSlot.FEET});
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player))
            return;
        if (player.getAbilities().instabuild)
            return;
        int CHECK_INTERVAL = 40;
        if (player.tickCount % CHECK_INTERVAL != 0)
            return;
        if (EnchantmentHelper.getEnchantmentLevel(RegisterEnch.CURSE_OF_PROVOCATION.get(),
                player) > 0) {
            provokeNearbyMobs(player);
        }
    }

    private static void provokeNearbyMobs(ServerPlayer player) {
        int radius = Math.max((Integer) getConfig("curseofprovocation",
                "radius", 1), 0);
        List<Entity> nearbyEntities = player.level.getEntities(player,
                player.getBoundingBox().inflate(radius),
                entity -> entity instanceof Mob && canFightBack((Mob) entity));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Mob mob) {
                mob.setLastHurtByPlayer(player);
                mob.setTarget(player);
            }
        }
    }

    private static boolean canFightBack(Mob mob) {
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