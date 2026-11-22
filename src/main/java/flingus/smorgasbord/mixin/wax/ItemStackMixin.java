package flingus.smorgasbord.mixin.wax;

import flingus.smorgasbord.index.ModDataComponentTypes;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract DataComponentMap getComponents();

    @Shadow
    public abstract <T> @Nullable T set(DataComponentType<T> dataComponentType, @Nullable T object);

    @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
    private void smorgasbord$changeGoggleHeight(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess, CallbackInfoReturnable<Boolean> cir) {
        if (clickAction != ClickAction.SECONDARY || !this.getComponents().has(ModDataComponentTypes.GOGGLE_HEIGHT)) return;

        int height = (this.getComponents().get(ModDataComponentTypes.GOGGLE_HEIGHT) + 1) % 4;
        this.set(ModDataComponentTypes.GOGGLE_HEIGHT, height);

        if (player.level().isClientSide()) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.6f, 0.85f + height * 0.15f);
        }

        cir.setReturnValue(true);
    }
}
