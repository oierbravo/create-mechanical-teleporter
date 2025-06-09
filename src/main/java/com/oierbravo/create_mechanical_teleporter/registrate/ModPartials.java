package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class ModPartials {
    public static final PartialModel BLOCK_CORE = block("mechanical_teleporter/core");
    public static final PartialModel BLOCK_CORE_GLOW = block("mechanical_teleporter/core_glow");

    public static final PartialModel BUTTON = PartialModel.of(ModConstants.asResource("block/hand_teleporter/button"));
    public static final PartialModel BUTTON_ACTIVE = PartialModel.of(ModConstants.asResource("block/hand_teleporter/button_active"));
    public static final PartialModel ANTENNA = PartialModel.of(ModConstants.asResource("block/hand_teleporter/antenna"));
    public static final PartialModel ANTENNA_ACTIVE = PartialModel.of(ModConstants.asResource("block/hand_teleporter/antenna_active"));




    private static PartialModel block(String path) {
        return PartialModel.of(ModConstants.asResource("block/" + path));
    }
    private static PartialModel item(String path) {
        return PartialModel.of(ModConstants.asResource("item/" + path));
    }
    public static void init() {
        // init static fields
    }
}
