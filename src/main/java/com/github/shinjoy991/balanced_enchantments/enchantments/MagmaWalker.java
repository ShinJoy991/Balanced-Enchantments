package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_BOOTS;

public class MagmaWalker extends Enchantment {

    public MagmaWalker() {
        super(Rarity.VERY_RARE, IS_BOOTS,
                new EquipmentSlotType[]{EquipmentSlotType.FEET});

    }

    public static void freezeNearby(LivingEntity living, ServerWorld worldIn, BlockPos pos,
            int level) {
        if (!living.isOnGround() || !living.isShiftKeyDown()) {
            return;
        }
        try {
            if (!((PlayerEntity) living).abilities.instabuild) {
                int random = new Random().nextInt(100);
                if (random < 5) {
                    ItemStack boots = living.getItemBySlot(EquipmentSlotType.FEET);
                    if (!boots.getItem().isFireResistant()) {
                        int damage = boots.getDamageValue();
                        boots.setDamageValue(damage + random);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        int minduration = Math.max((Integer) getConfig("magmawalker", "minduration", 1), 1);
        int maxduration = Math.max((Integer) getConfig("magmawalker", "maxduration", 1), 1);
        int radiusperlvl = (Integer) getConfig("magmawalker", "radiusperlvl", 1);
        int radius = Math.max(level * radiusperlvl, 0);
        BlockPos.betweenClosedStream(pos.offset((-radius), -1.0, (-radius)), pos.offset(radius,
                        -1.0D, radius))
                .filter(blockpos -> blockpos.closerThan(living.position(), radius))
                .filter(blockpos -> worldIn.getBlockState(blockpos.above()).is(Blocks.AIR))
                .filter(blockpos -> {
                    BlockState blockstate = worldIn.getBlockState(blockpos);
                    return blockstate.getMaterial() == Material.LAVA &&
                            blockstate.getBlock() instanceof FlowingFluidBlock &&
                            blockstate.getValue(FlowingFluidBlock.LEVEL) == 0;
                })
                .filter(blockpos -> {
                    BlockState blockstate = RegisterBlock.CRUSTEDMAGMA.get().defaultBlockState();
                    return blockstate.canSurvive(worldIn, blockpos) && worldIn.isUnobstructed(blockstate, blockpos, ISelectionContext.empty()) &&
                            !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(living,
                                    net.minecraftforge.common.util.BlockSnapshot.create(worldIn.dimension(), worldIn, blockpos), Direction.UP);
                })
                .forEach(blockpos -> {
                    worldIn.setBlockAndUpdate(blockpos,
                            RegisterBlock.CRUSTEDMAGMA.get().defaultBlockState());
                    worldIn.getBlockTicks().scheduleTick(blockpos,
                            RegisterBlock.CRUSTEDMAGMA.get(),
                            MathHelper.nextInt(living.getRandom(), minduration, maxduration));
                });
    }

    public int getMinCost(int enchantmentLevel) {
        return enchantmentLevel * 15;
    }

    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 25;
    }

    public boolean isTreasureOnly() {
        return true;
    }

    public int getMaxLevel() {
        return Math.max((Integer) getConfig("magmawalker", "maxlevel", 1), 1);
    }

    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && !(ench instanceof FrostWalkerEnchantment) && ench != Enchantments.DEPTH_STRIDER;
    }
}