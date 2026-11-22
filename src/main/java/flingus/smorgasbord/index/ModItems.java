package flingus.smorgasbord.index;

import flingus.smorgasbord.item.WaxPasteItem;
import flingus.smorgasbord.item.WetWipeItem;
import moriyashiine.strawberrylib.api.module.SLibRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.equipment.Equippable;

import java.util.List;

public class ModItems {
    public static final Item WAX_PASTE = SLibRegistries.registerItem("wax_paste", WaxPasteItem::new, new Item.Properties().enchantable(15).component(DataComponents.TOOL, new Tool(List.of(), 1.0F, 1, true)).durability(128));
    public static final Item WET_WIPE = SLibRegistries.registerItem("wet_wipe", WetWipeItem::new, new Item.Properties().enchantable(15).component(DataComponents.TOOL, new Tool(List.of(), 1.0F, 1, true)).durability(16));
    public static final Item WAX_GOGGLES = SLibRegistries.registerItem("wax_goggles", Item::new, new Item.Properties().stacksTo(1).component(ModDataComponentTypes.GOGGLE_HEIGHT, 1).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).build()));

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((itemGroup) -> {
                itemGroup.addAfter(Items.BONE_MEAL, WAX_PASTE, WET_WIPE);
                itemGroup.addAfter(Items.SADDLE, WAX_GOGGLES);
            }
        );
    }
}
