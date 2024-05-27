package com.github.shinjoy991.balanced_enchantments.mixin;

import com.github.shinjoy991.balanced_enchantments.enchantments.MagmaWalker;
import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlotType p_184582_1_);

    @Inject(method = "tryAddSoulSpeed", at = @At("HEAD"))
    private void magmaWalk(CallbackInfo ci) {
        try {
            int j = EnchantmentHelper.getItemEnchantmentLevel(RegisterEnch.MAGMA_WALKER.get(),
                    this.getItemBySlot(EquipmentSlotType.FEET));
            if (j > 0) {
                MagmaWalker.freezeNearby(((LivingEntity) (Object) this),
                        (ServerWorld) ((LivingEntity) (Object) this).level,
                        ((LivingEntity) (Object) this).blockPosition(), j);
            }
        } catch (Exception ignored) {
        }
    }
}