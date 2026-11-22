package flingus.smorgasbord.mixin.wax;

import flingus.smorgasbord.util.WaxUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.EyeblossomBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EyeblossomBlock.class)
public class EyeblossomBlockMixin {
    @Inject(method = "tryChangingState", at = @At("HEAD"), cancellable = true)
    private void smorgasbord$dontChangeState(BlockState state, ServerLevel level, BlockPos blockPos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if (WaxUtil.isWaxed(level, blockPos)) {
            cir.setReturnValue(false);
        }
    }
}