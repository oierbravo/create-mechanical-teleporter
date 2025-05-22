package com.oierbravo.create_mechanical_teleporter;

import com.mojang.logging.LogUtils;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterBlockEntity;
import com.oierbravo.create_mechanical_teleporter.content.logistics.GlobalTeleportersManager;
import com.oierbravo.create_mechanical_teleporter.foundation.ChunkManager;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.oierbravo.create_mechanical_teleporter.infrastructure.data.ModDataGen;
import com.oierbravo.create_mechanical_teleporter.registrate.*;
import com.oierbravo.mechanicals.utility.RegistrateLangBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import org.slf4j.Logger;

import static com.oierbravo.create_mechanical_teleporter.ModConstants.DISPLAY_NAME;
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
        ModMenuTypes.register();
        MConfigs.register(modLoadingContext,modContainer);



        ModCreativeTabs.register(modEventBus);
        modEventBus.addListener(ModMessages::registerNetworking);
        ModPackets.register();
        ModDataComponents.register(modEventBus);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerChunkLoaders);
        modEventBus.addListener(ModDataGen::gatherData);

        generateLangEntries();
    }

    private void generateLangEntries(){
        new RegistrateLangBuilder(MODID, registrate())
                .addCreativeTab(DISPLAY_NAME)
                .add("hand_teleporter.tooltip.clear", "Hold shift + right click to clear")
                .add("hand_teleporter.tooltip.address", "Address: %s")
                .add("hand_teleporter.message.cleared", "Frequency cleared")
                .add("ui.no_valid_teleporter", "No valid teleporter found")
                .addBlockTooltipCondition("mechanical_teleporter",0, "When placed")
                .addBlockTooltipBehaviour("mechanical_teleporter",0, "Generates a new frequency or uses the tuned one")
                .addBlockTooltipCondition("mechanical_teleporter",1,"R-Click on another Teleporter")
                .addBlockTooltipBehaviour("mechanical_teleporter",1,"Tunes to the teleporter network")

                .addItemTooltipSummary("hand_teleporter", "Holds one frequency")
                .addItemTooltipCondition("hand_teleporter",1,"When used")
                .addItemTooltipBehaviour("hand_teleporter",1,"Teleports to the __configured frequency__")
                .addItemTooltipCondition("hand_teleporter",2,"R-Click on Teleporter")
                .addItemTooltipBehaviour("hand_teleporter",2,"Tunes to the __teleporter__ network")
                .addItemTooltipCondition("hand_teleporter",3,"R-Click while Sneaking")
                .addItemTooltipBehaviour("hand_teleporter",3,"__Clears__ the frequency")
                .add("simple_teleport_controller.frequency_slot_1", "Freq. #1");

    }

    @net.neoforged.bus.api.SubscribeEvent
    public void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        TeleporterBlockEntity.registerCapabilities(event);
    }
    private void registerChunkLoaders(RegisterTicketControllersEvent event) {
        event.register(ChunkManager.TICKET_CONTROLLER);
    }
    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }

}
