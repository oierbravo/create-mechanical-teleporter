package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterBlockEntity;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterRenderer;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterVisual;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.creative.CreativeTeleporterBlockEntityBlockEntity;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.creative.CreativeTeleporterRenderer;
import com.oierbravo.create_mechanical_teleporter.content.logistics.manager.TeleporterManagerBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ModBlockEntities {
    public static final BlockEntityEntry<TeleporterBlockEntity> MECHANICAL_TELEPORTER = MechanicalTeleporter.registrate()
            .blockEntity("mechanical_teleporter", TeleporterBlockEntity::new)
            .visual(() -> TeleporterVisual::new)
            .validBlocks(ModBlocks.MECHANICAL_TELEPORTER)
            .renderer(() -> TeleporterRenderer::new)
            .register();

    public static final BlockEntityEntry<CreativeTeleporterBlockEntityBlockEntity> CREATIVE_TELEPORTER = MechanicalTeleporter.registrate()
            .blockEntity("creative_mechanical", CreativeTeleporterBlockEntityBlockEntity::new)
            .validBlocks(ModBlocks.CREATIVE_TELEPORTER)
            .renderer(() -> CreativeTeleporterRenderer::new)
            .register();

    public static final BlockEntityEntry<TeleporterManagerBlockEntity> TELEPORTER_MANAGER = MechanicalTeleporter.registrate()
            .blockEntity("teleporter_manager", TeleporterManagerBlockEntity::new)
            .validBlocks(ModBlocks.TELEPORTER_MANAGER)
            .register();

    public static void register() {}
}