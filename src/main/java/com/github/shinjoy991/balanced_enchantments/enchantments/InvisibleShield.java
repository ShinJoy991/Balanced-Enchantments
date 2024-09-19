package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.helpers.DelayFunc.delayedTask;

public class InvisibleShield extends Enchantment {

    private static final Map<UUID, Boolean> cooldowns = new HashMap<>();
    private static int percentreduction = 0;
    private static int recovertick = 0;

    public InvisibleShield() {
        super(Enchantment.Rarity.RARE, EnchantmentType.ARMOR,
                new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS,
                        EquipmentSlotType.FEET});
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        cooldowns.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onHit(LivingHurtEvent event) {
        int EnchantLevel;
        LivingEntity victim = event.getEntityLiving();
        if (victim == null)
            return;
        try {
            EnchantLevel =
                    EnchantmentHelper.getEnchantmentLevel(RegisterEnch.INVISIBLE_SHIELD.get(),
                            victim);
        } catch (Exception e) {
            return;
        }
        if (EnchantLevel <= 0)
            return;
        if (cooldowns.getOrDefault(victim.getUUID(), false))
            return;
        percentreduction =
                EnchantLevel * Math.max((Integer) getConfig("invisibleshield",
                                "percentreductionperlvl", 1), 0);
        if (percentreduction > 100) percentreduction = 100;
        recovertick =
                Math.max((Integer) getConfig("invisibleshield", "recovertick", 1), 0);
        cooldowns.put(victim.getUUID(), true);
        delayedTask(recovertick, () -> {
            cooldowns.remove(victim.getUUID());
            // Add particles
            World world = victim.level;
            if (!(world instanceof ServerWorld)) {
                return;
            }
            ServerWorld serverWorld = (ServerWorld) world;
            for (double theta = 0; theta < 360; theta += 12) {
                for (double phi = 0; phi < 180; phi += 12) {
                    double radiansTheta = Math.toRadians(theta);
                    double radiansPhi = Math.toRadians(phi);

                    double xOffset = 1.5 * Math.sin(radiansPhi) * Math.cos(radiansTheta);
                    double yOffsetSphere =
                            1.5 * Math.sin(radiansPhi) * Math.sin(radiansTheta);
                    double zOffset = 1.5 * Math.cos(radiansPhi);

                    double x = victim.getX() + xOffset;
                    double y = victim.getY() + 1 + yOffsetSphere;
                    double z = victim.getZ() + zOffset;

                    serverWorld.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, 0, 0, 0,
                            4.5);
                }
            }
            // Play sound
            if (victim instanceof ServerPlayerEntity)
                ((ServerPlayerEntity) victim).playNotifySound(SoundEvents.ENCHANTMENT_TABLE_USE,
                        victim.getSoundSource(), 1.0F, 1.0F);
        });
        event.setAmount(event.getAmount() * (1 - percentreduction * 0.01F));
    }

    public int getMinCost(int p_45083_) {
        return 20 + (p_45083_ - 1) * 2;
    }

    public int getMaxCost(int p_45085_) {
        return super.getMinCost(p_45085_) + 10;
    }

    public int getMaxLevel() {
        return Math.max((Integer) getConfig("invisibleshield", "maxlevel", 1), 1);
    }
}