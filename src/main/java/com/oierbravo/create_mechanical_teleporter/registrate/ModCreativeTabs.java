package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterBlock;
import com.oierbravo.mechanicals.utility.MechanicalRegistrateDisplayItemsGenerator;
import com.simibubi.create.AllCreativeModeTabs;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.oierbravo.create_mechanical_teleporter.ModConstants.MODID;

public class ModCreativeTabs {

    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TAB_REGISTER.register("main",
            () -> CreativeModeTab.builder()
                    .title(ModLang.creativeTab.t().component())
                    .displayItems(
                        MechanicalRegistrateDisplayItemsGenerator.create(true)
                                .withItems(MechanicalTeleporter.registrate().getAll(Registries.ITEM))
                                .withBlocks(MechanicalTeleporter.registrate().getAll(Registries.BLOCK))
                                .withVisibilitiesPost(itemTabVisibilityMap -> {
                                    for (BlockEntry<HandTeleporterBlock> entry : ModBlocks.HAND_TELEPORTERS) {
                                        HandTeleporterBlock block = entry.get();
                                        if (block.getColor() != DyeColor.GREEN) {
                                            itemTabVisibilityMap.put(entry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
                                        }
                                    }
                                })
                    )
                    .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getId())
                    .icon(ModBlocks.MECHANICAL_TELEPORTER::asStack)
                    .build());

    public static void register(IEventBus modEventBus) {
        TAB_REGISTER.register(modEventBus);
    }
}
