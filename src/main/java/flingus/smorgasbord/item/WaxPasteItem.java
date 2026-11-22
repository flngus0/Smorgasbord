package flingus.smorgasbord.item;

import flingus.smorgasbord.index.ModProperties;
import flingus.smorgasbord.util.WaxUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WaxPasteItem extends Item {
    public WaxPasteItem(Properties properties) {
        super(properties);
    }

    public static boolean isWaxLockable(BlockState state) {
        return ModProperties.supportsWaxed(state);
    }

    public static InteractionResult applyPaste(@Nullable Player player, Level level, InteractionHand hand, BlockHitResult blockHitResult) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState state = level.getBlockState(blockPos);
        ItemStack stack = player != null ? player.getItemInHand(hand) : ItemStack.EMPTY;

        if (HoneycombItem.WAXABLES.get().containsKey(state.getBlock())) {
            Block waxable = HoneycombItem.WAXABLES.get().get(state.getBlock());
            if (waxable != null) {
                BlockState waxedState = waxable.withPropertiesOf(state);

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.setBlock(blockPos, waxedState, Block.UPDATE_ALL);
                    serverLevel.levelEvent(null, 3003, blockPos, 0);
                    serverLevel.playSound(null, blockPos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                    damage(player, stack, hand);
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (!isWaxLockable(state) || ModProperties.isWaxed(state)) {
            return InteractionResult.PASS;
        }

        List<BlockPos> targets = WaxUtil.getConnectedPositions(state, blockPos);
        if (targets.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            for (BlockPos targetPos : targets) {
                BlockState targetState = level.getBlockState(targetPos);
                if (isWaxLockable(targetState) && !ModProperties.isWaxed(targetState)) {
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        }

        boolean waxed = false;
        for (BlockPos targetPos : targets) {
            BlockState targetState = serverLevel.getBlockState(targetPos);
            if (isWaxLockable(targetState) && !ModProperties.isWaxed(targetState)) {
                serverLevel.setBlock(targetPos, ModProperties.withWaxed(targetState, true), Block.UPDATE_ALL);
                serverLevel.levelEvent(null, 3003, targetPos, 0);
                waxed = true;
            }
        }

        if (!waxed) {
            return InteractionResult.PASS;
        }

        damage(player, stack, hand);
        serverLevel.playSound(null, blockPos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    private static void damage(@Nullable Player player, ItemStack itemStack, InteractionHand hand) {
        if (player != null && !player.isCreative() && !itemStack.isEmpty()) {
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            itemStack.hurtAndBreak(1, player, slot);
        }
    }
}