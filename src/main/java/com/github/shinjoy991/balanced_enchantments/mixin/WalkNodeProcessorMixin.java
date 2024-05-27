package com.github.shinjoy991.balanced_enchantments.mixin;

import com.github.shinjoy991.balanced_enchantments.register.RegisterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.pathfinding.WalkNodeProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WalkNodeProcessor.class)
public abstract class WalkNodeProcessorMixin {

    @Inject(
            method = "isBurningBlock",
            at = @At("RETURN"),
            cancellable = true)
    private static void modifyIsBurningBlock(BlockState p_237233_0_,
            CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || p_237233_0_.is(RegisterBlock.CRUSTEDMAGMA.get()));
    }
}