package com.oierbravo.create_mechanical_teleporter;

import com.mojang.logging.LogUtils;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportLinkNetworkHandler;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.oierbravo.create_mechanical_teleporter.registrate.*;
import com.oierbravo.mechanicals.utility.RegistrateLangBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.data.WorldAttached;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.slf4j.Logger;

import static com.oierbravo.create_mechanical_teleporter.ModConstants.DISPLAY_NAME;
import static com.oierbravo.create_mechanical_teleporter.ModConstants.MODID;

// The value here should match an entry in the META-INF/mods.toml file
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
    public static final TeleportLinkNetworkHandler TELEPORT_NETWORK_HANDLER = new TeleportLinkNetworkHandler();

    public MechanicalTeleporter(IEventBus modEventBus, ModContainer modContainer)
    {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        REGISTRATE.registerEventListeners(modEventBus);


        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();

        MConfigs.register(modLoadingContext,modContainer);


        ModCreativeTabs.register(modEventBus);
        modEventBus.addListener(ModMessages::registerNetworking);

        generateLangEntries();
    }

    private void generateLangEntries(){
        new RegistrateLangBuilder(MODID, registrate())
                .addCreativeTab(DISPLAY_NAME)
                .add("simple_teleport_controller.frequency_slot_1", "Freq. #1");

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

}
