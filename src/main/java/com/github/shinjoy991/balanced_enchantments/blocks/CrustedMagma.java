package com.github.shinjoy991.balanced_enchantments.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class CrustedMagma extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    public CrustedMagma() {
        super(Properties.of(Material.STONE,
                        MaterialColor.NETHER).requiresCorrectToolForDrops().lightLevel((state) -> 3)
                .randomTicks().strength(0.5F)
                .isValidSpawn((p_187421_, p_187422_, p_187423_, p_187424_) -> p_187424_.fireImmune())
                .hasPostProcess((p_61036_, p_61037_, p_61038_) -> true)
                .emissiveRendering((p_61036_, p_61037_, p_61038_) -> true)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public void turnIntoLava(World world, BlockPos pos) {
        world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
        world.neighborChanged(pos, Blocks.LAVA, pos);
    }

    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos,
            Random random) {
        this.tick(state, worldIn, pos, random);
        BlockPos blockpos = pos.above();
        if (worldIn.getFluidState(pos).is(FluidTags.WATER)) {
            worldIn.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
                    2.6F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.8F);
            worldIn.sendParticles(ParticleTypes.LARGE_SMOKE, (double) blockpos.getX() + 0.5D,
                    (double) blockpos.getY() + 0.25D, (double) blockpos.getZ() + 0.5D, 8, 0.5D,
                    0.25D, 0.5D, 0.0D);
        }
    }

    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int age = state.getValue(AGE);
        if (random.nextInt(3) == 0 || fewerNeigboursThan(world, pos, 4)) {
            if (world.getMaxLocalRawBrightness(pos) > 11 - age - state.getLightBlock(world, pos) && slightlyMelt(state, world, pos)) {
                for (Direction direction : Direction.values()) {
                    BlockPos newPos = pos.relative(direction);
                    BlockState newState = world.getBlockState(newPos);
                    if (newState.is(this) && !slightlyMelt(newState, world, newPos)) {
                        world.getBlockTicks().scheduleTick(newPos, this,
                                MathHelper.nextInt(random, 20, 40));
                    }
                }
                return;
            }
        }
        world.getBlockTicks().scheduleTick(pos, this, MathHelper.nextInt(random, 20, 40));
    }

    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block,
            BlockPos fromPos, boolean isMoving) {
        if (world instanceof ServerWorld && block.defaultBlockState().is(this) && fewerNeigboursThan((ServerWorld) world, pos, 2)) {
            turnIntoLava(world, pos);
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    private boolean fewerNeigboursThan(ServerWorld world, BlockPos pos, int limit) {
        int count = 0;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            if (world.getBlockState(neighborPos).is(this) && ++count >= limit) {
                return false;
            }
        }
        return true;
    }

    private boolean slightlyMelt(BlockState state, World worldIn, BlockPos pos) {
        int i = state.getValue(AGE);
        if (i < 3) {
            worldIn.setBlock(pos, state.setValue(AGE, i + 1), 2);
            return false;
        } else {
            this.turnIntoLava(worldIn, pos);
            return true;
        }
    }

    public void stepOn(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
        if (!p_176199_3_.fireImmune() && p_176199_3_ instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) p_176199_3_)) {
            p_176199_3_.hurt(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.stepOn(p_176199_1_, p_176199_2_, p_176199_3_);
    }
}
