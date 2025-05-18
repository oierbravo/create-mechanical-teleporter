package com.oierbravo.create_mechanical_teleporter.infrastructure.config;

import com.oierbravo.create_mechanical_teleporter.content.items.wand.TeleportWandConfigs;
import net.createmod.catnip.config.ConfigBase;

public class ModConfigServer extends ConfigBase {
    public final TeleportWandConfigs teleportWand = nested(0, TeleportWandConfigs::new, "Teleport wand");
    public final ModStress stressValues = nested(0, ModStress::new, "Stress values");

    @Override
    public String getName() {
        return "server";
    }
}
