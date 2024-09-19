package com.github.shinjoy991.balanced_enchantments.enchantments;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.helpers.DelayFunc.delayedTask;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_BOW_CROSSBOW;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.MIGHTY_FORCE;

public class MightyForce extends Enchantment {

    private static final Map<UUID, Integer> shoot = new HashMap<>();
    private static final Map<UUID, Boolean> delay = new HashMap<>();
    private static final Map<UUID, Boolean> delay1 = new HashMap<>();
    private static final Set<UUID> activeTasks = new HashSet<>();

    public MightyForce() {
        super(Rarity.VERY_RARE, IS_BOW_CROSSBOW,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        shoot.remove(event.getEntity().getUUID());
        delay.remove(event.getEntity().getUUID());
        delay1.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onMightyForceShoot(ArrowLooseEvent event) {
        int Level;
        if (!event.getBow().is(Items.BOW) && !event.getBow().is(Items.CROSSBOW))
            return;
        try {
            Level = EnchantmentHelper.getItemEnchantmentLevel(MIGHTY_FORCE.get(),
                    event.getBow());
        } catch (Exception e) {
            return;
        }
        Player player = event.getPlayer();
        UUID playerUUID = player.getUUID();
        if (delay.getOrDefault(playerUUID, false))
            return;
        if (Level <= 0) {
            shoot.remove(playerUUID);
            return;
        }
        shoot.putIfAbsent(playerUUID, 0);
        int currentval = shoot.get(playerUUID);
        if (currentval == 0) {
            shoot.put(playerUUID, currentval + 1);
            currentval = 1;
        }

        int stackdeletetick = Math.max((Integer) getConfig("mightyforce", "stackdeletetick", 1), 3);
        int finalCurrentval = currentval;

        removeShootPlayer(playerUUID, stackdeletetick, finalCurrentval);
        delay.put(playerUUID, true);
        delayedTask(2, () -> {
            delay.remove(playerUUID);
        });
    }

    private static void removeShootPlayer(UUID playerUUID, int stackdeletetick,
            int finalCurrentval) {
        if (activeTasks.contains(playerUUID)) {
            return;
        }
        activeTasks.add(playerUUID);
        delayedTask(stackdeletetick, () -> {

            if (shoot.getOrDefault(playerUUID, 0) < finalCurrentval + 1) {
                shoot.remove(playerUUID);
                activeTasks.remove(playerUUID);
            } else {
                activeTasks.remove(playerUUID);
                removeShootPlayer(playerUUID, stackdeletetick, shoot.get(playerUUID));
            }
        });
    }

    private static boolean isArrow(Item item) {
        return item instanceof ArrowItem;
    }

    @SubscribeEvent
    public static void onHit(LivingHurtEvent event) {
        DamageSource damageSource = event.getSource();
        LivingEntity victim = event.getEntityLiving();
        if (!(event.getSource().getDirectEntity() instanceof AbstractArrow))
            return;
        Entity shooter;
        try {
            shooter = damageSource.getEntity();
        } catch (Exception e) {
            return;
        }
        if (shooter == null || victim == null)
            return;
        if (!(shooter instanceof LivingEntity))
            return;
        if (shoot.getOrDefault(shooter.getUUID(), 0) == 0) {
            return;
        }
        float percentdmgincrease =
                (shoot.get(shooter.getUUID())) * 0.01F * Math.max((Integer) getConfig(
                        "mightyforce", "percentdmgincrease", 1), 0);
        event.setAmount(event.getAmount() * (1 + percentdmgincrease));
        shoot.put(shooter.getUUID(), shoot.get(shooter.getUUID()) + 1);
    }


    @SubscribeEvent
    public static void onArrowShoot(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide)
            return;
        Entity en = event.getEntity();
        if (!(en instanceof AbstractArrow))
            return;
        LivingEntity shooter;
        try {
            shooter = (LivingEntity) ((Arrow) en).getOwner();
            if (!(((Arrow) en).getOwner() instanceof LivingEntity))
                return;
        } catch (Exception e) {
            return;
        }
        try {
            if (!delay.getOrDefault(shooter.getUUID(), false))
                return;
        } catch (Exception e) {
            return;
        }
        if (shoot.getOrDefault(shooter.getUUID(), 0) == 0) {
            return;
        }
        int val = shoot.get(shooter.getUUID());
        if (shooter instanceof ServerPlayer player) {
            if (!player.getAbilities().instabuild) {
                int infinityLevel =
                        EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY_ARROWS, player);
                boolean hasInfinity = infinityLevel > 0;
                if (!hasInfinity) {
                    player.getInventory().clearOrCountMatchingItems(p -> isArrow(p.getItem()), 1,
                            player.inventoryMenu.getCraftSlots());
                }
            }
        }
        if (val >= 5)
            val = 5;
        en.getPersistentData().putInt("BEColorArrow", val);
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

    @Override
    public Component getFullname(int level) {
        return new TranslatableComponent(this.getDescriptionId()).withStyle(style -> style.withColor(TextColor.fromRgb(0xFFA500)));
    }

    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && !(ench instanceof DamageEnchantment);
    }
}