package com.github.shinjoy991.balanced_enchantments.events;

import com.github.shinjoy991.balanced_enchantments.helpers.NbtFunc;
import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.helpers.DelayFunc.delayedTask;
import static net.minecraft.world.item.BowItem.getPowerForTime;

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
        Player player = event.getEntity();
        ItemStack item = new ItemStack(Items.ARROW);
        int arrowCount = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == Items.ARROW) {
                arrowCount += stack.getCount();
            }
        }
        if (!player.getAbilities().instabuild) {
            if (arrowCount >= VolleyLevel * 2 + 1 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, event.getBow()) <= 0) {
                player.getInventory().clearOrCountMatchingItems(p -> item.getItem() == p.getItem(),
                        VolleyLevel * 2,
                        player.inventoryMenu.getCraftSlots());
            } else
                if (!(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS,
                        event.getBow()) > 0)) {
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
    public static void onArrowVolleyShoot(EntityJoinLevelEvent event) {
        Entity en = event.getEntity();
        if (!(en instanceof Arrow))
            return;
        if (en.getTags().contains("be.volley"))
            return;
        if (!(((Arrow) en).getOwner() instanceof LivingEntity shooter))
            return;
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
        CompoundTag oldnbt = en.serializeNBT();
        CompoundTag newNbt = NbtFunc.copyNBTWithoutUUID(oldnbt);
        Vec3 vector3d = shooter.getDeltaMovement();
        int anglebetween = (Integer) getConfig("volley", "anglebetween", 1);
        for (int i = 0; i < VolleyLevel * 2; i++) {

            int degree = -anglebetween * VolleyLevel + i * anglebetween;
            if (degree >= 0)
                degree = degree + anglebetween;
            double angleOffset = Math.toRadians(degree);
            Arrow newArrow = new Arrow(shooter.level, shooter);
            newArrow.deserializeNBT(newNbt);
            double motionX =
                    -Math.sin(shooter.getYRot() * Math.PI / 180.0 - angleOffset) * Math.cos(shooter.getXRot() * Math.PI / 180.0);
            double motionY = -Math.sin(shooter.getXRot() * Math.PI / 180.0);
            double motionZ =
                    Math.cos(shooter.getYRot() * Math.PI / 180.0 - angleOffset) * Math.cos(shooter.getXRot() * Math.PI / 180.0);
            if (k > 0) {
                newArrow.setKnockback(k);
            }
            newArrow.addTag("be.volley");
            newArrow.shoot(motionX, motionY, motionZ, arrowSpeed, 0);
            newArrow.setDeltaMovement(newArrow.getDeltaMovement().add(vector3d.x,
                    shooter.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
            shooter.level.addFreshEntity(newArrow);
            String removeSubArrow = (String) getConfig("volley", "removesubarrow", 0);
            if (removeSubArrow.equalsIgnoreCase("true")) {
                delayedTask(100, () -> {
                            try {
                                newArrow.discard();
                            } catch (Exception ignored) {}
                        }
                );
            }
        }
    }
}