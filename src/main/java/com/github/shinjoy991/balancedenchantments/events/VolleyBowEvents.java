package com.github.shinjoy991.balancedenchantments.events;

import com.github.shinjoy991.balancedenchantments.helpers.NbtFunc;
import com.github.shinjoy991.balancedenchantments.register.RegisterEnch;
import com.sun.javafx.embed.AbstractEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.shinjoy991.balancedenchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balancedenchantments.helpers.DelayFunc.delayedTask;
import static net.minecraft.item.BowItem.getPowerForTime;

public class VolleyBowEvents {
    private static final Map<UUID, Boolean> shoot = new HashMap<>();
    private static float arrowSpeed = 0;
    private static int level = 0;
    private static int k = 0;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onVolleyShoot(ArrowLooseEvent event) {
        int VolleyLevel;
        try {
            VolleyLevel = EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.VOLLEY.get(),
                    event.getBow());
        } catch (Exception e) {
            return;
        }
        if (VolleyLevel <= 0 || !event.hasAmmo())
            return;
        PlayerEntity player = event.getPlayer();
        ItemStack item = new ItemStack(Items.ARROW);
        int arrowCount = 0;
        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == Items.ARROW) {
                arrowCount += stack.getCount();
            }
        }
        if (!player.abilities.instabuild) {
            if (arrowCount >= VolleyLevel * 2 + 1 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, event.getBow()) <= 0) {
                player.inventory.clearOrCountMatchingItems(p -> item.getItem() == p.getItem(),
                        VolleyLevel * 2,
                        player.inventoryMenu.getCraftSlots());
            } else
                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS,
                        event.getBow()) > 0) {
                } else {
                    event.setCanceled(true);
                }
        }
        k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS,
                event.getBow());
        level = VolleyLevel;
        arrowSpeed = getPowerForTime(event.getCharge()) * 3.0f;
        shoot.put(player.getUUID(), true);
        delayedTask(2, () -> {
            shoot.remove(player.getUUID());
        });
    }

    @SubscribeEvent
    public static void onArrowVolleyShoot(EntityJoinWorldEvent event) {
        Entity en = event.getEntity();
        if (!(en instanceof ArrowEntity))
            return;
        if (en.getTags().contains("be.volley"))
            return;
        if (!(((ArrowEntity) en).getOwner() instanceof LivingEntity))
            return;
        LivingEntity shooter = (LivingEntity) ((ArrowEntity) en).getOwner();
        int VolleyLevel;
        int k;
        if (!shoot.getOrDefault(shooter.getUUID(), false)) {
            try {
               VolleyLevel =
                       EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.VOLLEY.get(),
                        shooter.getMainHandItem());
                k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS,
                        shooter.getMainHandItem());
                arrowSpeed = 1.6F;
            } catch (Exception e) {
                return;
            }
        } else {
            VolleyLevel = level;
            k = VolleyBowEvents.k;
        }
        shoot.remove(shooter.getUUID());
        en.addTag("be.volley");
        CompoundNBT oldnbt = en.serializeNBT();
        CompoundNBT newNbt = NbtFunc.copyNBTWithoutUUID(oldnbt);
        Vector3d vector3d = shooter.getDeltaMovement();
        int anglebetween = (Integer) getConfig("volley", "anglebetween", 1);
        for (int i = 0; i < VolleyLevel * 2; i++) {

            int degree = -anglebetween * VolleyLevel + i * anglebetween;
            if (degree >= 0)
                degree = degree + anglebetween;
            double angleOffset = Math.toRadians(degree);
            ArrowEntity newArrow = new ArrowEntity(shooter.level, shooter);
            newArrow.deserializeNBT(newNbt);
            double motionX =
                    -Math.sin(shooter.yRot * Math.PI / 180.0 - angleOffset) * Math.cos(shooter.xRot * Math.PI / 180.0);
            double motionY = -Math.sin(shooter.xRot * Math.PI / 180.0);
            double motionZ =
                    Math.cos(shooter.yRot * Math.PI / 180.0 - angleOffset) * Math.cos(shooter.xRot * Math.PI / 180.0);
            if (k > 0) {
                newArrow.setKnockback(k);
            }
            newArrow.addTag("be.volley");
            newArrow.shoot(motionX, motionY, motionZ, arrowSpeed, 0);
            newArrow.setDeltaMovement(newArrow.getDeltaMovement().add(vector3d.x,
                    shooter.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
            shooter.level.addFreshEntity(newArrow);
        }
    }
}