package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_PICKAXE;

public class Linking extends Enchantment {

    public Linking() {
        super(Rarity.COMMON, IS_PICKAXE,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null || !(event.getLevel() instanceof ServerLevel world))
            return;

        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();

        int level = EnchantmentHelper.getTagEnchantmentLevel(RegisterEnch.LINKING.get(), tool);
        if (level <= 0)
            return;
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE, tool);

        BlockPos pos = event.getPos();
        BlockState originalState = world.getBlockState(pos);
        Block block = originalState.getBlock();
        if (!block.builtInRegistryHolder().is(BlockTags.MINEABLE_WITH_PICKAXE))
            return;

        Queue<BlockPos> blocksToBreak = new ArrayDeque<>();
        blocksToBreak.add(pos);
        int blocksBroken = 0;
        List<BlockPos> offsets = new ArrayList<>(List.of(
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