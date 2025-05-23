package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterBlockEntity;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterRenderer;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterVisual;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.creative.CreativeTeleporterBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ModBlockEntities {
    public static final BlockEntityEntry<TeleporterBlockEntity> MECHANICAL_TELEPORTER = MechanicalTeleporter.registrate()
            .blockEntity("mechanical_teleporter", TeleporterBlockEntity::new)
            .visual(() -> TeleporterVisual::new)
            .validBlocks(ModBlocks.MECHANICAL_TELEPORTER)
            .renderer(() -> TeleporterRenderer::new)
            .register();

    public static final BlockEntityEntry<CreativeTeleporterBlockEntity> CREATIVE_TELEPORTER = MechanicalTeleporter.registrate()
            .blockEntity("creative_mechanical", CreativeTeleporterBlockEntity::new)
            .validBlocks(ModBlocks.CREATIVE_TELEPORTER)
            .register();

    public static void register() {}
}