package com.oierbravo.create_mechanical_teleporter;

import com.mojang.logging.LogUtils;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportLinkNetworkHandler;
import com.oierbravo.create_mechanical_teleporter.registrate.*;
import com.simibubi.create.AllContainerTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.WorldAttached;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MechanicalTeleporter.MODID)
public class MechanicalTeleporter
{
    public static final String MODID = "create_mechanical_teleporter";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final NonNullSupplier<CreateRegistrate> registrate = CreateRegistrate.lazy(MODID);

    public static final TeleportLinkNetworkHandler TELEPORT_LINK_NETWORK_HANDLER = new TeleportLinkNetworkHandler();

    public MechanicalTeleporter()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);

        ModBlocks.register();
        ModItems.register();
        ModTiles.register();
        ModContainerTypes.register();

        generateLangEntries();
    }

    private void setup(final FMLCommonSetupEvent event) {

        LOGGER.info("Create Teleport init!");
        ModPackets.registerPackets();
    }
    private void generateLangEntries(){
        registrate().addRawLang("create_mechanical_teleporter.simple_teleport_controller.frequency_slot_1", "Freq. #1");
        registrate().addRawLang("create_mechanical_teleporter.simple_teleport_controller.frequency_slot_2", "Freq. #2");
        registrate().addRawLang("create_mechanical_teleporter.simple_teleport_controller.bind_position", "Linked to %s");
        registrate().addRawLang("create_mechanical_teleporter.simple_teleport_controller.not_fast_enough", "Not enough speed in the destination.");
        registrate().addRawLang("create_mechanical_teleporter.simple_teleport_controller.overstressed", "Destination overstressed.");
        registrate().addRawLang("create_mechanical_teleporter.simple_teleport_controller.not_valid_fluid", "Liquid not valid in the destination.");
        registrate().addRawLang("create_mechanical_teleporter.simple_teleport_controller.not_liquid_enough", "Not enough liquid in the destination.");
        registrate().addRawLang("create_mechanical_teleporter.simple_teleport_controller.out_of_range", "Destination out of range.");
        //registrate().addRawLang("create_mechanical_extruder.ponder.extruder.header", "Block generation");
        //registrate().addRawLang("create_mechanical_extruder.ponder.extruder.text_1", "The Extruder uses rotational force to generate blocks");
        //registrate().addRawLang("create_mechanical_extruder.ponder.extruder.text_2", "Generation depends on side & below blocks.");
        //registrate().addRawLang("create_mechanical_extruder.ponder.extruder.text_3", "When the process is done, the result can be obtained via Right-click");
        //registrate().addRawLang("create_mechanical_extruder.ponder.extruder.text_4", "The outputs can also be extracted by automation");

    }
    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        MechanicalTeleporter.TELEPORT_LINK_NETWORK_HANDLER.onLoadWorld(world);
    }

    @SubscribeEvent
    public static void onUnloadWorld(LevelEvent.Unload event) {
        LevelAccessor world = event.getLevel();
        MechanicalTeleporter.TELEPORT_LINK_NETWORK_HANDLER.onUnloadWorld(world);
        WorldAttached.invalidateWorld(world);
    }
     public static CreateRegistrate registrate() {
        return registrate.get();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }

}
