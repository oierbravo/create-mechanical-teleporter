package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter;

import net.createmod.catnip.config.ConfigBase;

public class TeleporterConfigs  extends ConfigBase {
    public final ConfigBool autoChunkLoad = b(true, "autoChunkLoad", Comments.autoChunkLoad);
    public final ConfigInt autoChunkLoadRange = i(2, 1, "autoChunkLoadRange", Comments.autoChunkLoadRange);


    private static class Comments {
        static String autoChunkLoad = "Automatic force chunk load.";
        static String autoChunkLoadRange = "Range for the automatic force chunk loading.";
    }

    @Override
    public String getName() {
        return "mechanical_teleporter";
    }
}
