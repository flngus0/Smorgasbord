package flingus.smorgasbord;

import eu.midnightdust.lib.config.MidnightConfig;
import flingus.smorgasbord.index.ModDataComponentTypes;
import flingus.smorgasbord.index.ModItems;
import flingus.smorgasbord.index.ModProperties;
import flingus.smorgasbord.item.WaxPasteItem;
import flingus.smorgasbord.util.WaxUtil;
import moriyashiine.strawberrylib.api.SLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Smorgasbord implements ModInitializer {
    public static final String MOD_ID = "smorgasbord";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        SLib.init(MOD_ID);
        MidnightConfig.init(MOD_ID, SmorgasbordConfig.class);

        ModItems.init();
        ModDataComponentTypes.init();

        initEvents();
        LOGGER.info("Smorgasboard");
    }

    private void initEvents() {
        UseBlockCallback.EVENT.register((player, level, hand, blockHitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.is(ModItems.WAX_PASTE)) {
                return InteractionResult.PASS;
            }
            return WaxPasteItem.applyPaste(player, level, hand, blockHitResult);
        });

        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack itemStack = player.getItemInHand(hand);
            if (!itemStack.is(Items.PAPER)) return InteractionResult.PASS;

            Vec3 vec3 = player.getEyePosition();
            Vec3 vec32 = vec3.add(player.calculateViewVector(player.getXRot(), player.getYRot()).scale(player.blockInteractionRange()));

            BlockHitResult blockHitResult = level.clip(new ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, player));
            if (blockHitResult.getType() != HitResult.Type.BLOCK) return InteractionResult.PASS;

            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!level.mayInteract(player, blockPos)) return InteractionResult.PASS;

            if (level.getFluidState(blockPos).is(FluidTags.WATER)) {
                if (level instanceof ServerLevel serverLevel) {
                    boolean wasLastItem = itemStack.getCount() == 1 && !player.isCreative();
                    ItemStack result = ItemUtils.createFilledResult(itemStack, player, new ItemStack(ModItems.WET_WIPE));

                    if (wasLastItem) {
                        player.setItemInHand(hand, result);
                    }

                    player.awardStat(Stats.ITEM_USED.get(Items.PAPER));
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_SPLASH, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    serverLevel.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
                    player.inventoryMenu.broadcastChanges();
                }

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.AFTER.register((level, player, blockPos, blockState, blockEntity) -> {
            if (!(level instanceof ServerLevel serverLevel)) return;

            Set<BlockPos> targets = new LinkedHashSet<>(WaxUtil.getConnectedPositions(blockState, blockPos));
            Set<BlockPos> firstPass = new LinkedHashSet<>(targets);

            for (BlockPos pos : firstPass) {
                BlockState neighborState = serverLevel.getBlockState(pos);
                targets.addAll(WaxUtil.getConnectedPositions(neighborState, pos));
            }

            for (BlockPos pos : targets) {
                BlockState current = serverLevel.getBlockState(pos);
                if (ModProperties.isWaxed(current)) {
                    serverLevel.setBlock(pos, ModProperties.withWaxed(current, false), Block.UPDATE_ALL);
                }
            }
        });
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}