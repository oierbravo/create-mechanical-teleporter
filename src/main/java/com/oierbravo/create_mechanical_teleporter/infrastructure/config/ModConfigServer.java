package com.oierbravo.create_mechanical_teleporter.infrastructure.config;

import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterConfigs;
import com.oierbravo.create_mechanical_teleporter.content.items.wand.TeleportWandConfigs;
import com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters.mechanical.TeleporterConfigs;
import net.createmod.catnip.config.ConfigBase;

public class ModConfigServer extends ConfigBase {
    public final TeleportWandConfigs wand = nested(0, TeleportWandConfigs::new, "Teleport wand");
    public final HandTeleporterConfigs handTeleporter = nested(0, HandTeleporterConfigs::new, "Hand teleporter");
    public final TeleporterConfigs teleporter = nested(0, TeleporterConfigs::new, "Mechanical teleporter");
    public final MiscServerConfigs misc = nested(0, MiscServerConfigs::new, "Misc");

    public final ModStress stressValues = nested(0, ModStress::new, "Stress values");

    @Override
    public String getName() {
        return "server";
    }
}
