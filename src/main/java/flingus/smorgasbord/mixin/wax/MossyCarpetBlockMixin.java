package flingus.smorgasbord.mixin.wax;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import flingus.smorgasbord.util.WaxUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.MossyCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MossyCarpetBlock.class)
public class MossyCarpetBlockMixin {
    @ModifyReturnValue(method = "canSurvive", at = @At(value = "RETURN"))
    protected boolean canSurvive(boolean original, BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        if(levelReader instanceof ServerLevel serverLevel && WaxUtil.isWaxed(serverLevel, blockPos)) return true;

        return original;
    }
}
