package flingus.smorgasbord.client.compat.trinkets;

import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import flingus.smorgasbord.client.render.WaxGoggleTrinketRenderer;
import flingus.smorgasbord.index.ModItems;
import net.minecraft.client.Minecraft;

public final class WaxTrinketsCompat {
    private WaxTrinketsCompat() {}

    public static void init() {
        TrinketRendererRegistry.registerRenderer(ModItems.WAX_GOGGLES, new WaxGoggleTrinketRenderer());
    }

    public static boolean hasTrinket() {
        return TrinketsApi.getTrinketComponent(Minecraft.getInstance().player).map(c -> !c.getEquipped(ModItems.WAX_GOGGLES).isEmpty()).orElse(false);
    }
}
