package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_PICKAXE;

public class Linking extends Enchantment {

    public Linking() {
        super(Rarity.COMMON, IS_PICKAXE,
                new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null || !(event.getWorld() instanceof ServerWorld))
            return;
        ServerWorld world = (ServerWorld) event.getWorld();
        PlayerEntity player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();

        int level = EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.LINKING.get(), tool);
        if (level <= 0)
            return;
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);

        BlockPos pos = event.getPos();
        BlockState originalState = world.getBlockState(pos);
        Block block = originalState.getBlock();
        if (block.getHarvestTool(originalState) != net.minecraftforge.common.ToolType.PICKAXE)
            return;

        Queue<BlockPos> blocksToBreak = new ArrayDeque<>();
        blocksToBreak.add(pos);
        int blocksBroken = 0;
        List<BlockPos> offsets = new ArrayList<>(Arrays.asList(
                new BlockPos(-1, 0, 0), // West
                new BlockPos(1, 0, 0),  // East
                new BlockPos(0, -1, 0), // Down
                new BlockPos(0, 1, 0),  // Up
                new BlockPos(0, 0, -1), // North
                new BlockPos(0, 0, 1)   // South
        ));
        int addblockperlevel = Math.max((Integer) getConfig("linking",
                "addblockperlvl", 1), 0);
        while (!blocksToBreak.isEmpty() && blocksBroken < level * addblockperlevel) {

            BlockPos currentPos = blocksToBreak.poll();
            Collections.shuffle(offsets);
            if (currentPos.equals(pos)) {
                for (BlockPos offset : offsets) {
                    BlockPos adjacentPos = currentPos.offset(offset);
                    BlockState adjacentState = world.getBlockState(adjacentPos);
                    if (adjacentState.getBlock() == originalState.getBlock()) {
                        world.destroyBlock(adjacentPos, true, player);
                        blocksToBreak.add(adjacentPos);
                        blocksBroken++;
                        if (blocksBroken >= level * addblockperlevel)
                            break;
                    }
                }
            } else {
                for (BlockPos offset : offsets) {
                    BlockPos adjacentPos = currentPos.offset(offset);
                    BlockState adjacentState = world.getBlockState(adjacentPos);
                    if (adjacentState.getBlock() == originalState.getBlock()) {
                        world.destroyBlock(adjacentPos, true, player);
                        blocksToBreak.add(adjacentPos);
                        blocksBroken++;
                        if (blocksBroken >= level * addblockperlevel)
                            break;
                    }
                }
            }
        }
    }

    public int getMinCost(int enchantmentLevel) {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 30;
    }

    public int getMaxLevel() {
        return Math.max((Integer) getConfig("linking", "maxlevel", 1), 1);
    }
    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && ench != Enchantments.BLOCK_FORTUNE;
    }
}