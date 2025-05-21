package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterBlockEntity;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterRenderer;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ModBlockEntities {
    public static final BlockEntityEntry<TeleporterBlockEntity> MECHANICAL_TELEPORTER = MechanicalTeleporter.registrate()
            .blockEntity("mechanical_teleporter", TeleporterBlockEntity::new)
            .visual(() -> TeleporterVisual::new)
            .validBlocks(ModBlocks.MECHANICAL_TELEPORTER)
            .renderer(() -> TeleporterRenderer::new)
            .register();

    public static void register() {}
}