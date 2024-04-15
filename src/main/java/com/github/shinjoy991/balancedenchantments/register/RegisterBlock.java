package com.github.shinjoy991.balancedenchantments.register;

import com.github.shinjoy991.balancedenchantments.BalancedEnchantments;
import com.github.shinjoy991.balancedenchantments.blocks.CrustedMagma;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegisterBlock {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BalancedEnchantments.MOD_ID);
    public static final RegistryObject<Block> CRUSTEDMAGMA =
            BLOCKS.register("crusted_magma_block", CrustedMagma::new);
}