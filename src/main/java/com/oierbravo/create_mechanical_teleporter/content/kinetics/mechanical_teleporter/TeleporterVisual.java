package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter;

import com.oierbravo.mechanicals.foundation.visual.HalfShaftVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction;

public class TeleporterVisual extends HalfShaftVisual<TeleporterBlockEntity> implements SimpleDynamicVisual {

    public TeleporterVisual(VisualizationContext context, TeleporterBlockEntity blockEntity, float partialTick, Direction direction) {
        super(context, blockEntity, partialTick, direction);
    }

    public TeleporterVisual(VisualizationContext visualizationContext, TeleporterBlockEntity teleporterBlockEntity, float partialTick) {
        super(visualizationContext, teleporterBlockEntity, partialTick, Direction.NORTH);
    }

    @Override
    public void beginFrame(Context ctx) {

    }
}
