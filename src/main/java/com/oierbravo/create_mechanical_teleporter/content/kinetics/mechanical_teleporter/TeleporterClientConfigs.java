package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter;

import net.createmod.catnip.config.ConfigBase;

public class TeleporterClientConfigs extends ConfigBase {
    public final ConfigBool disableAddressRender = b(false, "disableAddressRender", Comments.disableAddressRender);
    public final ConfigBool alwaysShowAddress = b(true, "alwaysShowName", Comments.alwaysShowAddress);

    private static class Comments {
        static String alwaysShowAddress = "Always shows the configured address over the block.";
        static String disableAddressRender = "Disables Address render over the teleporters.";
    }

    @Override
    public String getName() {
        return "mechanical_teleporter";
    }
}
