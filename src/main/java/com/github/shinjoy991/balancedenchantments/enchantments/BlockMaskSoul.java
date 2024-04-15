package com.github.shinjoy991.balancedenchantments.enchantments;

import com.github.shinjoy991.balancedenchantments.register.RegisterEnch;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.github.shinjoy991.balancedenchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balancedenchantments.helpers.DelayFunc.delayedTask;
import static com.github.shinjoy991.balancedenchantments.register.RegisterEnch.IS_HOE;

public class BlockMaskSoul extends Enchantment {

    private static final Map<UUID, Boolean> cooldowns = new HashMap<>();

    public BlockMaskSoul() {
        super(Rarity.RARE, IS_HOE,
                new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    public int getMinCost(int enchantmentLevel) {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 50;
    }

    public int getMaxLevel() {
        return Math.max((Integer) getConfig("blockmasksoul", "maxlevel", 1), 1);
    }

    public void doPostAttack(LivingEntity user, Entity target, int level) {

        if (cooldowns.getOrDefault(user.getUUID(), false))
            return;
        if (Math.max((Integer) getConfig("blockmasksoul", "chance", 1), 0) <= new Random().nextInt(100) + 1)
            return;
        int EnchantLevel = 0;
        try {
            EnchantLevel =
                    EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.BLOCK_MASK_SOUL.get(),
                            user.getMainHandItem());
        } catch (Exception e) {
            return;
        }
        if (EnchantLevel <= 0)
            return;
        if (!(user.getMainHandItem().getItem() instanceof HoeItem)) {

            String onlyhoe = (String) getConfig("blockmasksoul", "onlyhoe", 0);
            if (onlyhoe.equalsIgnoreCase("true")) {
                return;
            }
        }
        if (!(target instanceof LivingEntity))
            return;
        World targetworld = target.level;
        if (targetworld.getServer() == null)
            return;
        BlockPos blockBelow = target.blockPosition().below();
        if (target.position().y % 1 != 0) {
            blockBelow = target.blockPosition();
        }
        Map<BlockPos, CompoundNBT> blockdata = new HashMap<>();
        Map<BlockPos, BlockState> originalStates = new HashMap<>();
        int offsetvar = Math.max(level - 1, 0);
        int i = 0;
        for (int xOffset = -offsetvar; xOffset <= offsetvar; xOffset++) {
            for (int zOffset = -offsetvar; zOffset <= offsetvar; zOffset++) {
                BlockPos pos = blockBelow.offset(xOffset, 0, zOffset);
                BlockState originalState = targetworld.getBlockState(pos);
                if (originalState.canOcclude() && isnot_excludeBlocks(originalState) && !originalState.getBlock().is(Blocks.AIR)) {
                    if (blockBelow.closerThan(pos, level)) {
                        originalStates.put(pos, originalState);
                        TileEntity blockEntity = targetworld.getBlockEntity(pos);
                        if (blockEntity != null) {
                            targetworld.removeBlockEntity(pos);
                            blockdata.put(pos, blockEntity.save(new CompoundNBT()));
                        } else {
                            blockdata.put(pos, null);
                        }
                        i++;
                        targetworld.setBlock(pos, Blocks.SOUL_SAND.defaultBlockState(), 3);
                    }
                }
            }
        }
        if (i > 0) {
            cooldowns.put(user.getUUID(), true);
        }
        int tickduration = Math.max((Integer) getConfig("blockmasksoul", "tickduration", 1), 1);
        delayedTask(tickduration, () -> {
                    for (Map.Entry<BlockPos, BlockState> entry : originalStates.entrySet()) {
                        BlockPos pos = entry.getKey();
                        BlockState originalState = entry.getValue();
                        CompoundNBT nbt = blockdata.get(pos);
                        BlockState newState = targetworld.getBlockState(pos);
                        if (newState.is(Blocks.SOUL_SAND) || !newState.is(Blocks.SOUL_SAND) && newState.is(Blocks.AIR)) {
                            targetworld.setBlockAndUpdate(pos, originalState);
                            if (nbt != null) {
                                TileEntity tileEntity = targetworld.getBlockEntity(pos);
                                if (tileEntity != null) {
                                    tileEntity.load(originalState, nbt);
                                }
                            }
                        }
                    }
                    cooldowns.remove(user.getUUID());
                }
        );
    }

    private boolean isnot_excludeBlocks(BlockState state) {
        Block[] blocks = new Block[]{
                Blocks.OBSIDIAN,
                Blocks.BEDROCK,
                Blocks.REDSTONE_LAMP,
                Blocks.COMMAND_BLOCK,
                Blocks.PISTON,
                Blocks.PISTON_HEAD,
                Blocks.RESPAWN_ANCHOR,
                Blocks.BARRIER,
                Blocks.END_PORTAL_FRAME,
                Blocks.STICKY_PISTON,
                Blocks.FARMLAND,
                Blocks.STRUCTURE_BLOCK,
                Blocks.JIGSAW,
                Blocks.FARMLAND,
                Blocks.CHAIN_COMMAND_BLOCK,
                Blocks.REPEATING_COMMAND_BLOCK,
        };
        for (Block block : blocks) {
            if (state.getBlock().is(block)) {
                return false;
            }
        }
        return true;
    }
}