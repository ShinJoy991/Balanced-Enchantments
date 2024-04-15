package com.github.shinjoy991.balancedenchantments.mixin;

import com.github.shinjoy991.balancedenchantments.register.RegisterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityType.class)
public class EntityTypeMixin {

    @Inject(
            method = "isBlockDangerous",
            at = @At(
                    value = "RETURN",
                    target = "Lnet/minecraft/entity/EntityType;isBlockDangerous" +
                            "(Lnet/minecraft/block/BlockState;)Z",
                    ordinal = 1
            ),
            cancellable = true)
    private void onisBlockDangerous(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || state.is(RegisterBlock.CRUSTEDMAGMA.get()));
    }
}