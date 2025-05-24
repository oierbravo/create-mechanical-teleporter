package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import net.createmod.catnip.config.ConfigBase;

public class HandTeleporterConfigs extends ConfigBase {

    public final ConfigInt cooldown = i(20, 1, "cooldown", Comments.cooldown);
    public final ConfigBool useAir = b(false,"useAir", Comments.useAir);
    public final ConfigInt airAmount = i(30, 1, "airAmount", Comments.airAmount);
    public final ConfigBool useDurability = b(false,"useDurability", Comments.useDurability);
    public final ConfigInt durabilityAmount = i(1, 1, "durabilityAmount", Comments.durabilityAmount);
    public final ConfigBool useXp = b(false,"useXp", Comments.useXp);
    public final ConfigInt xpAmount = i(100, 1, "xpAmount", Comments.xpAmount);



    private static class Comments {
        static String cooldown = "Cooldown after usage.";
        static String useAir = "Uses air to work.";
        static String airAmount = "How many air units consumes.";
        static String useDurability = "Uses durability to work.";
        static String durabilityAmount = "How much damage get when used air units consumes.";
        static String useXp = "Uses experience to work.";
        static String xpAmount = "How much experience consumes.";

    }

    @Override
    public String getName() {
        return "hand_teleporter";
    }
}
