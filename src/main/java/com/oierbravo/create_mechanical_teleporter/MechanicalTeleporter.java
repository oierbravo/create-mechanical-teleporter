package com.oierbravo.create_mechanical_teleporter;

import com.mojang.logging.LogUtils;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportLinkNetworkHandler;
import com.oierbravo.create_mechanical_teleporter.registrate.*;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
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

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    public static final TeleportLinkNetworkHandler TELEPORT_NETWORK_HANDLER = new TeleportLinkNetworkHandler();

    public MechanicalTeleporter()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);

        REGISTRATE.registerEventListeners(modEventBus);


        new ModCreativeTab("main");
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();

        //ModContainerTypes.register();

        generateLangEntries();
    }

    private void setup(final FMLCommonSetupEvent event) {

        LOGGER.info("Create Teleport init!");
        ModPackets.registerPackets();
    }
    private void generateLangEntries(){
        registrate().addRawLang("create_mechanical_teleporter.simple_teleport_controller.frequency_slot_1", "Freq. #1");
    }
    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        MechanicalTeleporter.TELEPORT_NETWORK_HANDLER.onLoadWorld(world);
    }

    @SubscribeEvent
    public static void onUnloadWorld(LevelEvent.Unload event) {
        LevelAccessor world = event.getLevel();
        MechanicalTeleporter.TELEPORT_NETWORK_HANDLER.onUnloadWorld(world);
        WorldAttached.invalidateWorld(world);
    }
    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }

}
