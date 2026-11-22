package flingus.smorgasbord.util;

import flingus.smorgasbord.index.ModProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.ArrayList;
import java.util.List;

public class WaxUtil {
    public static List<BlockPos> getConnectedPositions(BlockState mainState, BlockPos mainPos) {
        List<BlockPos> positions = new ArrayList<>();
        positions.add(mainPos);

        if (mainState.getBlock() instanceof DoorBlock) {
            if (mainState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                DoubleBlockHalf half = mainState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
                positions.add(half == DoubleBlockHalf.LOWER ? mainPos.above() : mainPos.below());
            }
        }
        else if (mainState.getBlock() instanceof ChestBlock) {
            if (mainState.hasProperty(BlockStateProperties.CHEST_TYPE) && mainState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                ChestType type = mainState.getValue(BlockStateProperties.CHEST_TYPE);

                if (type != ChestType.SINGLE) {
                    Direction facing = mainState.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    Direction connected = type == ChestType.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
                    positions.add(mainPos.relative(connected));
                }
            }
        }

        return positions;
    }

    public static boolean isWaxed(Level level, BlockPos blockPos) {
        return ModProperties.isWaxed(level.getBlockState(blockPos));
    }
}