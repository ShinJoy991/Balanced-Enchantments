package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import com.sun.javafx.geom.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
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
                new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        delay.remove(event.getEntity().getUUID());
    }
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        if (!(player instanceof ServerPlayerEntity))
            return;
        if (!(event.getWorld() instanceof ServerWorld))
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
        Vector3d lookDirection = player.getLookAngle().normalize(); // 1.16.5 uses Vec3d and getLookVec()


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