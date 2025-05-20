package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.global.NewTeleporterBlockEntity;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.global.NewTeleporterRenderer;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.global.NewTeleporterVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ModBlockEntities {
    public static final BlockEntityEntry<NewTeleporterBlockEntity> MECHANICAL_TELEPORTER = MechanicalTeleporter.registrate()
            .blockEntity("mechanical_teleporter", NewTeleporterBlockEntity::new)
            .visual(() -> NewTeleporterVisual::new)
            .validBlocks(ModBlocks.MECHANICAL_TELEPORTER)
            .renderer(() -> NewTeleporterRenderer::new)
            .register();

    public static void register() {}
}