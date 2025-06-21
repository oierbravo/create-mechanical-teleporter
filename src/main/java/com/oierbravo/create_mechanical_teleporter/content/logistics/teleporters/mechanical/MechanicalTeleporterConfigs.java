package com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters.mechanical;

import net.createmod.catnip.config.ConfigBase;

public class MechanicalTeleporterConfigs extends ConfigBase {
    public final ConfigBool autoChunkLoad = b(true, "autoChunkLoad", Comments.autoChunkLoad);
    public final ConfigInt autoChunkLoadRange = i(2, 1, "autoChunkLoadRange", Comments.autoChunkLoadRange);
    public final ConfigInt requiredFluidAmount = i(500,1,"requiredFluidAmount", Comments.requiredFluidAmount);

    private static class Comments {
        static String autoChunkLoad = "Automatic force chunk load.";
        static String autoChunkLoadRange = "Range for the automatic force chunk loading.";
        static String requiredFluidAmount = "Required fluid ammount.";
    }

    @Override
    public String getName() {
        return "mechanical_teleporter";
    }
}
