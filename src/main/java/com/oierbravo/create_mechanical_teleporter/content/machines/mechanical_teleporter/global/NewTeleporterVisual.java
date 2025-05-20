package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.global;

import com.oierbravo.mechanicals.foundation.visual.HalfShaftVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction;

public class NewTeleporterVisual extends HalfShaftVisual<NewTeleporterBlockEntity> implements SimpleDynamicVisual {

    public NewTeleporterVisual(VisualizationContext context, NewTeleporterBlockEntity blockEntity, float partialTick, Direction direction) {
        super(context, blockEntity, partialTick, direction);
    }

    public NewTeleporterVisual(VisualizationContext visualizationContext, NewTeleporterBlockEntity teleporterBlockEntity, float partialTick) {
        super(visualizationContext, teleporterBlockEntity, partialTick, Direction.NORTH);
    }

    @Override
    public void beginFrame(Context ctx) {

    }
}
