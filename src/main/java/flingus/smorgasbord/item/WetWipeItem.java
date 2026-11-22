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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WetWipeItem extends Item {
    public WetWipeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        BlockHitResult blockHitResult = new BlockHitResult(useOnContext.getClickLocation(), useOnContext.getClickedFace(), useOnContext.getClickedPos(), useOnContext.isInside());
        return applyWipe(useOnContext.getPlayer(), useOnContext.getLevel(), useOnContext.getHand(), blockHitResult);
    }

    public static InteractionResult applyWipe(@Nullable Player player, Level level, InteractionHand hand, BlockHitResult blockHitResult) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState state = level.getBlockState(blockPos);

        if (!WaxPasteItem.isWaxLockable(state)) {
            return InteractionResult.PASS;
        }

        List<BlockPos> directTargets = WaxUtil.getConnectedPositions(state, blockPos);
        if (directTargets.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            for (BlockPos targetPos : directTargets) {
                if (ModProperties.isWaxed(level.getBlockState(targetPos))) {
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        }

        Set<BlockPos> targets = new LinkedHashSet<>(directTargets);
        for (BlockPos targetPos : directTargets) {
            targets.addAll(WaxUtil.getConnectedPositions(serverLevel.getBlockState(targetPos), targetPos));
        }

        boolean unwaxed = false;
        for (BlockPos targetPos : targets) {
            BlockState targetState = serverLevel.getBlockState(targetPos);
            if (ModProperties.isWaxed(targetState)) {
                serverLevel.setBlock(targetPos, ModProperties.withWaxed(targetState, false), Block.UPDATE_ALL);
                serverLevel.levelEvent(null, 3004, targetPos, 0);
                unwaxed = true;
            }
        }

        if (!unwaxed) {
            return InteractionResult.PASS;
        }

        if (player != null && !player.isCreative()) {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                stack.hurtAndBreak(1, player, slot);

                if (stack.isEmpty()) {
                    player.setItemInHand(hand, new ItemStack(Items.PAPER));
                }
            }
        }

        serverLevel.playSound(null, blockPos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }
}