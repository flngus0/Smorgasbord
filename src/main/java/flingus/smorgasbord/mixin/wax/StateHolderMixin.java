package flingus.smorgasbord.mixin.wax;

import flingus.smorgasbord.index.ModProperties;
import flingus.smorgasbord.util.WaxUtil;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;

@Mixin(StateHolder.class)
public abstract class StateHolderMixin<O, S> {
    @Shadow @Final
    private Reference2ObjectArrayMap<Property<?>, Comparable<?>> values;

    @Inject(method = "getProperties", at = @At("HEAD"), cancellable = true)
    private void smorgasbord$getProperties(CallbackInfoReturnable<Collection<Property<?>>> cir) {
        if (!WaxUtil.frozen && this.values.containsKey(ModProperties.WAXED)) {
            cir.setReturnValue(this.values.keySet().stream().filter(prop -> prop != ModProperties.WAXED).toList());
        }
    }

    @Inject(method = "getValues", at = @At("HEAD"), cancellable = true)
    private void smorgasbord$getValues(CallbackInfoReturnable<Map<Property<?>, Comparable<?>>> cir) {
        if (!WaxUtil.frozen && this.values.containsKey(ModProperties.WAXED)) {
            Reference2ObjectArrayMap<Property<?>, Comparable<?>> filtered = new Reference2ObjectArrayMap<>(this.values);
            filtered.remove(ModProperties.WAXED);
            cir.setReturnValue(filtered);
        }
    }
}