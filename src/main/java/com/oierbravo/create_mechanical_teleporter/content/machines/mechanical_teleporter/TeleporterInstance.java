package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class TeleporterInstance  extends ShaftInstance<TeleporterBlockEntity> implements DynamicInstance {
    public TeleporterInstance(MaterialManager dispatcher, TeleporterBlockEntity blockEntity) {
        super(dispatcher, blockEntity);
    }

    @Override
    public void beginFrame() {

    }
    @Override
    protected Instancer<RotatingData> getModel() {


        BlockState referenceState = blockState.rotate(blockEntity.getLevel(), blockEntity.getBlockPos(), Rotation.CLOCKWISE_180);
        return getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, referenceState, Direction.DOWN);
    }
}
