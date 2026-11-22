package flingus.smorgasbord.mixin.wax;

import flingus.smorgasbord.util.WaxUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlock.class)
public class FallingBlockMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void smorgasbord$dontTick(BlockState state, ServerLevel level, BlockPos blockPos, RandomSource random, CallbackInfo ci) {
        if (WaxUtil.isWaxed(level, blockPos)) {
            ci.cancel();
        }
    }
}