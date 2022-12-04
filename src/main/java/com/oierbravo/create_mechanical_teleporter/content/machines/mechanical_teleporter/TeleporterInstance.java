package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;
import com.simibubi.create.content.contraptions.relays.encased.ShaftInstance;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TeleporterInstance  extends ShaftInstance implements DynamicInstance {
    public TeleporterInstance(MaterialManager dispatcher, KineticTileEntity tile) {
        super(dispatcher, tile);
    }

    @Override
    public void beginFrame() {

    }
    @Override
    protected Instancer<RotatingData> getModel() {


        BlockState referenceState = blockState.rotate(blockEntity.getLevel(), blockEntity.getBlockPos(), Rotation.CLOCKWISE_180);
        return getRotatingMaterial().getModel(AllBlockPartials.SHAFT_HALF, referenceState, Direction.DOWN);
    }
}
