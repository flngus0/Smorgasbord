package flingus.smorgasbord.mixin.wax;

import com.llamalad7.mixinextras.sugar.Local;
import flingus.smorgasbord.index.ModProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;create(Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/StateDefinition$Factory;)Lnet/minecraft/world/level/block/state/StateDefinition;"))
    private void smorgasbord$addWaxedPropertyToBuilder(BlockBehaviour.Properties properties, CallbackInfo ci, @Local StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ModProperties.WAXED);
    }

    @ModifyVariable(method = "registerDefaultState", at = @At("HEAD"), argsOnly = true)
    private BlockState smorgasbord$forceUnwaxedDefault(BlockState state) {
        if (state != null && state.hasProperty(ModProperties.WAXED)) {
            return state.setValue(ModProperties.WAXED, false);
        }
        return state;
    }
}