package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.helpers.DelayFunc.delayedTask;

public class HuntingInstinct extends Enchantment {

    private static final Set<UUID> cooldowns = new HashSet<>();

    public HuntingInstinct() {
        super(Rarity.VERY_RARE, EnchantmentType.WEAPON,
                new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
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
                for (ServerWorld world : server.getAllLevels()) {
                    for (Entity entity : world.getAllEntities()) {
                        if (entity instanceof LivingEntity) {
                        UUID entityId = entity.getUUID();
                        if (!cooldowns.contains(entityId) && hasHuntingInstinct((LivingEntity) entity)) {
                            int slownesslvl = Math.max((Integer) getConfig("huntinginstinct",
                                    "slownesslvl", 1), 0);
                            ((LivingEntity) entity).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200,
                                    slownesslvl, true, false));
                            cooldowns.add(entityId);
                            delayedTask(198, () -> cooldowns.remove(entityId));
                        }
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
        World targetworld = target.level;
        if (targetworld.getServer() == null)
            return;
        int strengthlvl = Math.max((Integer) getConfig("huntinginstinct",
                "strengthlvl", 1), 0);
        int speedlvl = Math.max((Integer) getConfig("huntinginstinct",
                "speedlvl", 1), 0);
        user.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 200, strengthlvl, true, false));
        user.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 200, speedlvl, true, false));
        user.removeEffect(Effects.MOVEMENT_SLOWDOWN);
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
    public ITextComponent getFullname(int level) {
        return new TranslationTextComponent(this.getDescriptionId()).withStyle(TextFormatting.GOLD);
    }

    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && ench != RegisterEnch.SUPER_CHARGED.get();
    }
}