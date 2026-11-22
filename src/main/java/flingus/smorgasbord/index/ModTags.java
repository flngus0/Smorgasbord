package flingus.smorgasbord.index;

import flingus.smorgasbord.Smorgasbord;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> WAX_LOCKABLE = create("wax_lockable");

    private static TagKey<Block> create(String string) {
        return TagKey.create(Registries.BLOCK, Smorgasbord.id(string));
    }
}
