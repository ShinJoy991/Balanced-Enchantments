package com.github.shinjoy991.balanced_enchantments.register;

import com.github.shinjoy991.balanced_enchantments.BalancedEnchantments;
import com.github.shinjoy991.balanced_enchantments.blocks.CrustedMagma;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegisterBlock {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BalancedEnchantments.MOD_ID);
    public static final RegistryObject<Block> CRUSTEDMAGMA =
            BLOCKS.register("crusted_magma_block", CrustedMagma::new);
}