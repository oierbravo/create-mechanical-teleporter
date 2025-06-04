package com.oierbravo.create_mechanical_teleporter;

import com.mojang.logging.LogUtils;
import com.oierbravo.create_mechanical_teleporter.content.logistics.GlobalTeleportersManager;
import com.oierbravo.create_mechanical_teleporter.foundation.ChunkManager;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.oierbravo.create_mechanical_teleporter.infrastructure.data.ModDataGen;
import com.oierbravo.create_mechanical_teleporter.registrate.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import org.slf4j.Logger;

import static com.oierbravo.create_mechanical_teleporter.ModConstants.MODID;

@Mod(MODID)
public class MechanicalTeleporter
{
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID).defaultCreativeTab(ModCreativeTabs.MAIN_TAB.getKey());
    static {
        REGISTRATE.setTooltipModifierFactory(item ->
                new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
        );
    }
    public static final GlobalTeleportersManager TELEPORTERS = new GlobalTeleportersManager();

    public MechanicalTeleporter(IEventBus modEventBus, ModContainer modContainer)
    {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        REGISTRATE.registerEventListeners(modEventBus);

        ChunkManager.init();

        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModFluids.register();
        ModMenuTypes.register();
        MConfigs.register(modLoadingContext,modContainer);



        ModCreativeTabs.register(modEventBus);
        modEventBus.addListener(ModMessages::registerNetworking);
        ModPackets.register();
        ModDataComponents.register(modEventBus);
        modEventBus.addListener(this::common);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerChunkLoaders);
        modEventBus.addListener(ModDataGen::gatherData);

        modEventBus.addListener(this::doClientStuff);


        generateLangEntries();
    }
    public void common(final FMLCommonSetupEvent event) {
        ModFluids.registerFluidInteractions();
    }




    private void generateLangEntries(){
        ModLang.register();
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {

    }
    private void registerChunkLoaders(RegisterTicketControllersEvent event) {
        event.register(ChunkManager.TICKET_CONTROLLER);
    }
    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ModPartials.init();
        //PonderIndex.addPlugin(new ModPonderPlugin());
    }
}
