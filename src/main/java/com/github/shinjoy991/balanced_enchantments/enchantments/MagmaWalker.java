package com.github.shinjoy991.balanced_enchantments.enchantments;

import com.github.shinjoy991.balanced_enchantments.register.RegisterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.Random;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.IS_BOOTS;

public class MagmaWalker extends Enchantment {

    public MagmaWalker() {
        super(Rarity.VERY_RARE, IS_BOOTS,
                new EquipmentSlot[]{EquipmentSlot.FEET});

    }

    public static void freezeNearby(LivingEntity living, ServerLevel worldIn, BlockPos pos,
            int level) {
        if (!living.onGround() || !living.isShiftKeyDown()) {
            return;
        }
        try {
            if (!((Player) living).getAbilities().instabuild) {
                int random = new Random().nextInt(100);
                if (random < 5) {
                    ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);
                    if (!boots.getItem().isFireResistant()) {
                        int damage = boots.getDamageValue();
                        boots.setDamageValue(damage + random);
                    }
                }
            }
        } catch (Exception ignored) {}
        int minduration = Math.max((Integer) getConfig("magmawalker", "minduration", 1), 1);
        int maxduration = Math.max((Integer) getConfig("magmawalker", "maxduration", 1), 1);
        int radiusperlvl = (Integer) getConfig("magmawalker", "radiusperlvl", 1);
        int radius = Math.max(level * radiusperlvl, 0);
        BlockPos.betweenClosedStream(pos.offset((-radius), -1, (-radius)), pos.offset(radius,
                        -1, radius))
                .filter(blockpos -> blockpos.closerToCenterThan(living.position(), radius))
                .filter(blockpos -> worldIn.getBlockState(blockpos.above()).is(Blocks.AIR))
                .filter(blockpos -> {
                    BlockState blockstate = worldIn.getBlockState(blockpos);
                    return blockstate.getFluidState().getType() == Fluids.LAVA &&
                            blockstate.getBlock() instanceof LiquidBlock &&
                            blockstate.getValue(LiquidBlock.LEVEL) == 0;
                })
                .filter(blockpos -> {
                    BlockState blockstate = RegisterBlock.CRUSTEDMAGMA.get().defaultBlockState();
                    return blockstate.canSurvive(worldIn, blockpos) && worldIn.isUnobstructed(blockstate, blockpos, CollisionContext.empty()) &&
                            !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(living,
                                    net.minecraftforge.common.util.BlockSnapshot.create(worldIn.dimension(), worldIn, blockpos), Direction.UP);
                })
                .forEach(blockpos -> {
                    worldIn.setBlockAndUpdate(blockpos,
                            RegisterBlock.CRUSTEDMAGMA.get().defaultBlockState());
                    worldIn.scheduleTick(blockpos,
                            RegisterBlock.CRUSTEDMAGMA.get(),
                            Mth.nextInt(living.getRandom(), minduration, maxduration));
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