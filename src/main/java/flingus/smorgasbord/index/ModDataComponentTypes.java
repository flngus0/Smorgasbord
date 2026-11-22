package flingus.smorgasbord.index;

import com.mojang.serialization.Codec;
import flingus.smorgasbord.Smorgasbord;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModDataComponentTypes {
    public static final DataComponentType<Integer> GOGGLE_HEIGHT = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Smorgasbord.id("goggle_height"),
            DataComponentType.<Integer>builder().persistent(Codec.INT).build()
    );

    public static void init(){}
}
