package com.oierbravo.create_mechanical_teleporter.infrastructure.config;

import net.createmod.catnip.config.ConfigBase;

public class MiscServerConfigs extends ConfigBase {
    public final ConfigInt enderFluidTeleportRange = i(5, 1, "enderFluidTeleportRange", Comments.enderFluidTeleportRange);

    private static class Comments {
        static String enderFluidTeleportRange = "Range for the ender fluid teleportation.";
    }

    @Override
    public String getName() {
        return "other";
    }
}
