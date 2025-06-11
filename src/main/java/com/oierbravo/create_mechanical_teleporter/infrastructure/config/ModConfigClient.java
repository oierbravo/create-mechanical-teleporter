package com.oierbravo.create_mechanical_teleporter.infrastructure.config;

import com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters.mechanical.TeleporterClientConfigs;
import net.createmod.catnip.config.ConfigBase;

public class ModConfigClient extends ConfigBase {
    public final TeleporterClientConfigs teleporter = nested(0, TeleporterClientConfigs::new, "Mechanical teleporter");

    private static class Comments {
    }

    @Override
    public String getName() {
        return "client";
    }
}
