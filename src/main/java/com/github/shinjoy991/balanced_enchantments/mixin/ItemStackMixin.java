package com.github.shinjoy991.balanced_enchantments.mixin;

import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Random;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract ItemStack copy();

    @ModifyVariable(method = "hurtAndBreak", at = @At("HEAD"), ordinal = 0)
    private int hurtAndBreak(int p_41623_) {
        ItemStack item = this.copy();
        try {
            if (EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.CURSE_OF_DURABILITY.get(),
                    item) > 0) {
                int minrandom = (Integer) getConfig("curseofdurability", "minrandom", 1);
                int maxrandom = (Integer) getConfig("curseofdurability", "maxrandom", 1);
                p_41623_ = new Random().nextInt(maxrandom - minrandom + 1) + minrandom;
            }
        } catch (Exception ignored) {
        }
        return p_41623_;
    }
}