package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter;

import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import net.minecraft.core.Direction;

public class TeleporterModeSlot extends CenteredSideValueBoxTransform {
    public TeleporterModeSlot() {
        super((state, d) -> d == Direction.UP);
    }
}
