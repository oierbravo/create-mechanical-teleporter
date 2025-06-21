package com.oierbravo.create_mechanical_teleporter.infrastructure.config;

import com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters.mechanical.MechanicalTeleporterClientConfigs;
import net.createmod.catnip.config.ConfigBase;

public class ModConfigClient extends ConfigBase {
    public final MechanicalTeleporterClientConfigs teleporter = nested(0, MechanicalTeleporterClientConfigs::new, "Mechanical teleporter");

    private static class Comments {
    }

    @Override
    public String getName() {
        return "client";
    }
}
