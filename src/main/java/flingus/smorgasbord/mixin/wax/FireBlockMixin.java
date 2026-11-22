package flingus.smorgasbord.mixin.wax;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import flingus.smorgasbord.util.WaxUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    @WrapMethod(method = "checkBurnOut")
    private void smorgasbord$skipBurnOutWhenWaxed(Level level, BlockPos blockPos, int chance, RandomSource random, int age, Operation<Void> original) {
        if (WaxUtil.isWaxed(level, blockPos)) {
            return;
        }
        original.call(level, blockPos, chance, random, age);
    }
}