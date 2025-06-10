package com.oierbravo.create_mechanical_teleporter.infrastructure.config;

import net.createmod.catnip.config.ConfigBase;

public class MiscServerConfigs extends ConfigBase {
    public final ConfigInt enderFluidTeleportRange = i(5, 1, "enderFluidTeleportRange", Comments.enderFluidTeleportRange);
    public final ConfigInt enderPotatoTeleportRange = i(32, 1, "enderFluidTeleportRange", Comments.enderPotatoTeleportRange);

    private static class Comments {
        static String enderFluidTeleportRange = "Range for the ender fluid teleportation.";
        static String enderPotatoTeleportRange = "Range for the Ender potato teleportation.";
    }

    @Override
    public String getName() {
        return "other";
    }
}
