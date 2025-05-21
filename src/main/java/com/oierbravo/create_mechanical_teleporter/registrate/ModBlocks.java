package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterBlock;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterBlockItem;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.ModStress;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

@SuppressWarnings("removal")
public class ModBlocks {


    private static final CreateRegistrate REGISTRATE = MechanicalTeleporter.registrate();

    public static final BlockEntry<TeleporterBlock> MECHANICAL_TELEPORTER = REGISTRATE.block("mechanical_teleporter", TeleporterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.METAL))
            .properties(p -> p.lightLevel($ -> 5))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .addLayer(() -> RenderType::translucent)
            .transform(ModStress.setImpact(8.0))
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .item(TeleporterBlockItem::new)
            .transform(customItemModel("_", "block"))
            .register();


    public static void register() {}
}
