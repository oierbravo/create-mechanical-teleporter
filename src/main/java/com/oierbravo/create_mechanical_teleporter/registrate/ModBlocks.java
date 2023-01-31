package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterBlock;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.simibubi.create.AllTags.pickaxeOnly;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class ModBlocks {


    private static final CreateRegistrate REGISTRATE = MechanicalTeleporter.registrate()
            .creativeModeTab(() ->  Create.BASE_CREATIVE_TAB);



    static {
        REGISTRATE.startSection(AllSections.LOGISTICS);
    }
    public static final BlockEntry<TeleporterBlock> MECHANICAL_TELEPORTER = REGISTRATE.block("mechanical_teleporter", TeleporterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.lightLevel($ -> 5))
            .properties(p -> p.color(DyeColor.ORANGE.getMaterialColor()))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .transform(BlockStressDefaults.setImpact(64.0))
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .addLayer(() -> RenderType::translucent)
            .item()
            .transform(customItemModel("_", "block"))
            .register();


    public static void register() {
        MechanicalTeleporter.createRregistrate().addToSection(MECHANICAL_TELEPORTER, AllSections.LOGISTICS);
    }
}
