package flingus.smorgasbord.mixin.wax;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import flingus.smorgasbord.index.ModItems;
import flingus.smorgasbord.index.ModProperties;
import flingus.smorgasbord.item.WaxPasteItem;
import flingus.smorgasbord.util.WaxUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @WrapMethod(method = "placeBlock")
    private boolean smorgasbord$smorgasbordPlace(BlockPlaceContext context, BlockState state, Operation<Boolean> original) {
        boolean placed = original.call(context, state);
        if (!placed) {
            return false;
        }

        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player == null) {
            return true;
        }

        ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (!offhandStack.is(ModItems.WAX_PASTE)) {
            return true;
        }

        BlockPos pos = context.getClickedPos();
        BlockState currentState = level.getBlockState(pos);
        if (!WaxPasteItem.isWaxLockable(currentState) || ModProperties.isWaxed(currentState)) {
            return true;
        }

        List<BlockPos> targets = WaxUtil.getConnectedPositions(currentState, pos);
        boolean didWax = false;

        for (BlockPos targetPos : targets) {
            BlockState targetState = level.getBlockState(targetPos);
            if (WaxPasteItem.isWaxLockable(targetState) && !ModProperties.isWaxed(targetState)) {
                didWax = true;
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.setBlock(targetPos, ModProperties.withWaxed(targetState, true), Block.UPDATE_ALL);
                    serverLevel.levelEvent(null, 3003, targetPos, 0);
                }
            }
        }

        if (didWax) {
            player.swing(InteractionHand.OFF_HAND, true);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) {
                    offhandStack.hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
                }
            }
        }

        return true;
    }
}