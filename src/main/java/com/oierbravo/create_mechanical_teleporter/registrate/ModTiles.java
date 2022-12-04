package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterInstance;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterRenderer;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterTile;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
public class ModTiles {
    public static final BlockEntityEntry<TeleporterTile> MECHANICAL_TELEPORTER = MechanicalTeleporter.registrate()
            .tileEntity("mechanical_teleporter", TeleporterTile::new)
            .instance(() -> TeleporterInstance::new)
            .validBlocks(ModBlocks.MECHANICAL_TELEPORTER)
            .renderer(() -> TeleporterRenderer::new)
            .register();

    public static void register() {}
}