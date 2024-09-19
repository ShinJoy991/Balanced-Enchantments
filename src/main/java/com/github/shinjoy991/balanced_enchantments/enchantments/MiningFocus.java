package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.helpers.DelayFunc.delayedTask;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_TOOL;

public class MiningFocus extends Enchantment {

    private static final Map<UUID, BlockState> lastBlockState = new HashMap<>();
    private static final Map<UUID, Integer> consecutiveBlocksMined = new HashMap<>();
    private static final Map<UUID, Boolean> cooldowns = new HashMap<>();

    public MiningFocus() {
        super(Rarity.COMMON, IS_TOOL,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        cooldowns.remove(event.getEntity().getUUID());
        consecutiveBlocksMined.remove(event.getEntity().getUUID());
        lastBlockState.remove(event.getEntity().getUUID());
    }
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer))
            return;
        ItemStack heldItem = player.getMainHandItem();
        int level = EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.MINING_FOCUS.get(),
                heldItem);
        if (level <= 0)
            return;

        BlockState currentBlockState = event.getState();
        UUID playerUUID = player.getUUID();
        int lastcount;
        if (lastBlockState.containsKey(playerUUID) && lastBlockState.get(playerUUID).equals(currentBlockState)) {
            int count = consecutiveBlocksMined.getOrDefault(playerUUID, 0) + 1;
            consecutiveBlocksMined.put(playerUUID, count);
            lastcount = count;
            if (cooldowns.getOrDefault(player.getUUID(), false))
                return;
            if (count >= 10) {

                int addtickperlevel = Math.max((Integer) getConfig("miningfocus",
                        "addtickperlvl", 1), 0);
                int hastelevel = Math.max((Integer) getConfig("miningfocus",
                        "hastelvl", 1), 0);
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,
                        addtickperlevel * level, hastelevel,
                        false, false));

                for (int i = 0; i < 360; i += 10) {
                    double angle = Math.toRadians(i);
                    double xOffset = Math.cos(angle);
                    double zOffset = Math.sin(angle);
                    Vec3 particlePos = player.position().add(xOffset, 1.0, zOffset);
                    ((ServerPlayer) player).serverLevel().sendParticles(ParticleTypes.INSTANT_EFFECT,
                            particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0);
                }

                ((ServerPlayer) player).serverLevel().playSound(null, player.blockPosition(),
                        SoundEvents.NOTE_BLOCK_BELL.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);

                consecutiveBlocksMined.remove(playerUUID);
                cooldowns.put(playerUUID, true);
                int cooldown = Math.max((Integer) getConfig("miningfocus",
                        "cooldown", 1), 0);
                delayedTask(cooldown, () -> {cooldowns.remove(playerUUID);
                });
            }
        } else {
            lastcount = 1;
            consecutiveBlocksMined.put(playerUUID, 1);
        }
        lastBlockState.put(playerUUID, currentBlockState);
        delayedTask(60, () -> {
            if (consecutiveBlocksMined.containsKey(playerUUID) && consecutiveBlocksMined.get(playerUUID) < lastcount + 1) {
                consecutiveBlocksMined.remove(playerUUID);
                lastBlockState.remove(playerUUID);
            }
        });
    }

    public int getMinCost(int p_45083_) {
        return 10 + (p_45083_ - 1) * 8;
    }

    public int getMaxCost(int p_45085_) {
        return super.getMinCost(p_45085_) + 10;
    }

    public int getMaxLevel() {
        return Math.max((Integer) getConfig("miningfocus", "maxlevel", 1), 1);
    }
}