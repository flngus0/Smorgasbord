package flingus.smorgasbord.index;

import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ModProperties {
    public static final BooleanProperty WAXED = BooleanProperty.create("waxed");

    private ModProperties() {
    }

    public static boolean supportsWaxed(BlockState state) {
        return state.is(ModTags.WAX_LOCKABLE) || state.getBlock() instanceof FallingBlock;
    }

    public static boolean isWaxed(BlockState state) {
        return supportsWaxed(state) && state.hasProperty(WAXED) && state.getValue(WAXED);
    }

    public static BlockState withWaxed(BlockState state, boolean waxed) {
        return supportsWaxed(state) && state.hasProperty(WAXED) ? state.setValue(WAXED, waxed) : state;
    }
}
