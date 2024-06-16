package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.helpers.DelayFunc.delayedTask;

public class IceAspect extends Enchantment {

    public IceAspect() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public void doPostAttack(LivingEntity user, Entity target, int level) {

        if (Math.max((Integer) getConfig("iceaspect", "chance", 1), 0) <= new Random().nextInt(100) + 1)
            return;
        int EnchantLevel = 0;
        try {
            EnchantLevel =
                    EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.ICE_ASPECT.get(),
                            user.getMainHandItem());
        } catch (Exception e) {
            return;
        }
        if (EnchantLevel <= 0)
            return;
        if (!(target instanceof LivingEntity))
            return;
        Level targetworld = target.level();
        if (targetworld.getServer() == null)
            return;
        int freezeduration = Math.max((Integer) getConfig("iceaspect",
                "freezedurationperlvl", 1), 0);
        if (freezeduration > 0)
            target.setTicksFrozen(140 + EnchantLevel * freezeduration);
        BlockPos blockBelow = target.blockPosition().below();
        if (target.position().y % 1 != 0) {
            blockBelow = target.blockPosition();
        }
        blockBelow = blockBelow.above();
        List<BlockPos> plusPatternPositions = new ArrayList<>();
        plusPatternPositions.add(blockBelow);
        plusPatternPositions.add(blockBelow.above());
        plusPatternPositions.add(blockBelow.offset(1, 0, 0));
        plusPatternPositions.add(blockBelow.offset(-1, 0, 0));
        plusPatternPositions.add(blockBelow.offset(0, 0, 1));
        plusPatternPositions.add(blockBelow.offset(0, 0, -1));

        for (BlockPos pos : plusPatternPositions) {
            BlockState originalState = targetworld.getBlockState(pos);

            if (originalState.getBlock().equals(Blocks.AIR)) {
                targetworld.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                delayedTask(40, () -> {
                    targetworld.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                });
            }
        }
    }

    public int getMinCost(int p_45000_) {
        return 10 + 20 * (p_45000_ - 1);
    }

    public int getMaxCost(int p_45002_) {
        return super.getMinCost(p_45002_) + 50;
    }

    public int getMaxLevel() {
        return Math.max((Integer) getConfig("iceaspect", "maxlevel", 1), 1);
    }

    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && ench != Enchantments.FIRE_ASPECT;
    }
}