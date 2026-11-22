package flingus.smorgasbord.client.render;

import flingus.smorgasbord.SmorgasbordConfig;
import flingus.smorgasbord.client.compat.trinkets.WaxTrinketsCompat;
import flingus.smorgasbord.index.ModItems;
import flingus.smorgasbord.index.ModProperties;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;

public class WaxedBlockOutlineRenderer {
    private static final int RADIUS = 16;

    public static void init() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            Minecraft client = Minecraft.getInstance();

            if(!SmorgasbordConfig.renderWax) return;

            if (client.player == null || client.level == null) return;

            if (!(FabricLoader.getInstance().isModLoaded("trinkets") && WaxTrinketsCompat.hasTrinket()) && !client.player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.WAX_GOGGLES)) return;

            BlockPos playerPos = client.player.blockPosition();
            int minChunkX = (playerPos.getX() - RADIUS) >> 4;
            int maxChunkX = (playerPos.getX() + RADIUS) >> 4;
            int minChunkZ = (playerPos.getZ() - RADIUS) >> 4;
            int maxChunkZ = (playerPos.getZ() + RADIUS) >> 4;

            int minY = Math.max(client.level.getMinY(), playerPos.getY() - RADIUS);
            int maxY = Math.min(client.level.getMaxY(), playerPos.getY() + RADIUS);

            float pulse = (float) (Math.sin(client.level.getGameTime() * 0.12) * 0.5 + 0.5);
            int alpha = (int) (pulse * 45);
            int color = (alpha << 24) | 0xF7BD28;

            for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                    if (!client.level.getChunkSource().hasChunk(chunkX, chunkZ)) continue;

                    LevelChunk chunk = client.level.getChunk(chunkX, chunkZ);
                    int startX = Math.max(chunk.getPos().getMinBlockX(), playerPos.getX() - RADIUS);
                    int endX = Math.min(chunk.getPos().getMaxBlockX(), playerPos.getX() + RADIUS);
                    int startZ = Math.max(chunk.getPos().getMinBlockZ(), playerPos.getZ() - RADIUS);
                    int endZ = Math.min(chunk.getPos().getMaxBlockZ(), playerPos.getZ() + RADIUS);

                    for (int x = startX; x <= endX; x++) {
                        for (int z = startZ; z <= endZ; z++) {
                            for (int y = minY; y <= maxY; y++) {
                                BlockPos blockPos = new BlockPos(x, y, z);
                                if (client.player.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) > RADIUS * RADIUS) continue;

                                BlockState state = chunk.getBlockState(blockPos);
                                if (!ModProperties.isWaxed(state)) continue;

                                var shape = state.getShape(client.level, blockPos);
                                for (AABB aabb : shape.toAabbs()) {
                                    Gizmos.cuboid(aabb.move(blockPos), GizmoStyle.fill(color));
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}