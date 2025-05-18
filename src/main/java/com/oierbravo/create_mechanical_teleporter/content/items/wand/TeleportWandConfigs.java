package com.oierbravo.create_mechanical_teleporter.content.items.wand;

import net.createmod.catnip.config.ConfigBase;

public class TeleportWandConfigs extends ConfigBase {
    public final int version = 1;
    public final ConfigInt range = i(20, 1, "range", Comments.range);
    public final ConfigInt airAmount = i(30, 1, "airAmount", Comments.airAmount);
    public final ConfigInt cooldown = i(20, 1, "cooldown", Comments.cooldown);


    private static class Comments {
        static String range = "Teleport range.";
        static String airAmount = "How many air units consumes.";
        static String cooldown = "Cooldown after usage.";
    }

    @Override
    public String getName() {
        return "teleport_wand";
    }
}
