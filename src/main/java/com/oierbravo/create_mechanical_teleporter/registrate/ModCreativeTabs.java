package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.simibubi.create.AllCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeTabs {

    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MechanicalTeleporter.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TAB_REGISTER.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.create_mechanical_extruder:main"))
                    .withTabsBefore(AllCreativeModeTabs.BUILDING_BLOCKS_TAB.getId())
                    .icon(ModBlocks.MECHANICAL_TELEPORTER::asStack)
                    .displayItems((pParameters, pOutput) -> {
                        for (RegistryEntry<Block> entry : MechanicalTeleporter.REGISTRATE.getAll(Registries.BLOCK)) {
                            pOutput.accept(entry.get());
                        }
                        for (RegistryEntry<Item> entry : MechanicalTeleporter.REGISTRATE.getAll(Registries.ITEM)) {
                            pOutput.accept(entry.get());
                        }
                    })
                    .build());

    public static CreativeModeTab getBaseTab() {
        return MAIN_TAB.get();
    }

    public static void register(IEventBus modEventBus) {
        TAB_REGISTER.register(modEventBus);
    }
}
