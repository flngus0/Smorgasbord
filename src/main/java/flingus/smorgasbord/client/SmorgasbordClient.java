package flingus.smorgasbord.client;

import flingus.smorgasbord.client.compat.trinkets.WaxTrinketsCompat;
import flingus.smorgasbord.client.render.WaxedBlockOutlineRenderer;
import flingus.smorgasbord.index.ModDataComponentTypes;
import flingus.smorgasbord.util.WaxUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class SmorgasbordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WaxedBlockOutlineRenderer.init();

        initEvents();

        if(FabricLoader.getInstance().isModLoaded("trinkets")) {
            WaxTrinketsCompat.init();
        }

        ItemTooltipCallback.EVENT.register((stack, context, type, tooltip) -> {
            Integer height = stack.get(ModDataComponentTypes.GOGGLE_HEIGHT);

            if (height != null) {
                tooltip.add(1, Component.translatable("item.smorgasbord.wax_goggles.height", height).withStyle(ChatFormatting.GRAY));
            }
        });
    }

    private void initEvents() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> WaxUtil.frozen = true);
    }
}
