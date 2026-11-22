package flingus.smorgasbord.mixin.wax;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import flingus.smorgasbord.util.WaxUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin {
    @WrapMethod(method = "serverTick")
    private void smorgasbord$pauseServerTick(ServerLevel serverLevel, BlockPos pos, Operation<Void> original) {
        if (WaxUtil.isWaxed(serverLevel, pos)) {
            return;
        }
        original.call(serverLevel, pos);
    }

    @WrapMethod(method = "clientTick")
    private void smorgasbord$pauseClientTick(Level level, BlockPos pos, Operation<Void> original) {
        if (WaxUtil.isWaxed(level, pos)) {
            return;
        }
        original.call(level, pos);
    }
}