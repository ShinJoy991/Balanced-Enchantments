package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
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

public class AutoMachine extends Enchantment {
    private static final Map<UUID, Boolean> delay = new HashMap<>();

    public AutoMachine() {
        super(Rarity.RARE, IS_TOOL,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        delay.remove(event.getEntity().getUUID());
    }
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer))
            return;
        if (!(event.getLevel() instanceof ServerLevel))
            return;
        int level = EnchantmentHelper.getEnchantmentLevel(RegisterEnch.AUTO_MACHINE.get(),
                player);
        if (level <= 0)
            return;
        if (delay.getOrDefault(player.getUUID(), false))
            return;
        delay.put(player.getUUID(), true);
        delayedTask(2, () -> {
            delay.remove(player.getUUID());
        });
        if (!player.isSteppingCarefully())
            return;
        int initiallength = Math.max((Integer) getConfig("automachine",
                "initiallength", 1), 0);
        int addlengthperlevel = Math.max((Integer) getConfig("automachine",
                "addlengthperlvl", 1), 0);

        BlockState initialBlockState = event.getState();
        BlockPos initialPos = event.getPos();
        Vec3 lookDirection = player.getLookAngle().normalize();

        int tunnelLength = initiallength + level * addlengthperlevel;
        int blocksBroken = 0;

        for (float i = 1; i <= tunnelLength + 0.3F; i += 0.4F) {
            BlockPos targetPos = initialPos.offset((int) (lookDirection.x * i),
                    (int) (lookDirection.y * i), (int) (lookDirection.z * i));
            for (int y = 0; y <= 1; y++) {
                BlockPos pos = targetPos.below(y);
                BlockState blockState = player.level.getBlockState(pos);

                if (blockState.getBlock() == initialBlockState.getBlock()
                        && blockState.getBlock() != Blocks.AIR
                        && blockState.getBlock().defaultDestroyTime() >= 0
                        && blockState.canOcclude()
                        && blockState.getBlock().getExplosionResistance() < 100
                        && BlockMaskSoul.isnot_excludeBlocks(blockState)) {
                    player.level.destroyBlock(pos, true);
                    blocksBroken++;
                    if (blocksBroken >= tunnelLength * 2) {
                        return;
                    }
                } else
                    if (blockState.getBlock() != initialBlockState.getBlock()
                            && blockState.getBlock() != Blocks.AIR
                    ) {
                        return;
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
        return Math.max((Integer) getConfig("automachine", "maxlevel", 1), 1);
    }
}