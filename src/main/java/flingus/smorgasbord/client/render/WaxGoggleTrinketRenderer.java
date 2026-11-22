package flingus.smorgasbord.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import flingus.smorgasbord.index.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class WaxGoggleTrinketRenderer implements TrinketRenderer {
    private final ItemStackRenderState itemRenderState = new ItemStackRenderState();
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public void render(ItemStack itemStack, SlotReference slotReference, EntityModel<? extends LivingEntityRenderState> entityModel, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, LivingEntityRenderState livingEntityRenderState, float v, float v1) {
        if(!(entityModel instanceof HumanoidModel<?> humanoidModel)) return;
        poseStack.pushPose();
        humanoidModel.head.translateAndRotate(poseStack);

        poseStack.translate(0.0f, -0.25f, 0.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        poseStack.scale(0.625f, -0.625f, -0.625f);

        client.getItemModelResolver().updateForTopItem(this.itemRenderState, itemStack, ItemDisplayContext.HEAD, null, null, 0);

        if (!client.player.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && !client.player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.WAX_GOGGLES)) {
            poseStack.scale(1.1f, 1.1f, 1.1f);
        }

        this.itemRenderState.submit(poseStack, submitNodeCollector, i, OverlayTexture.NO_OVERLAY, livingEntityRenderState.outlineColor);

        poseStack.popPose();
    }
}
