package com.oierbravo.create_mechanical_teleporter.content.logistics.manager;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TeleporterManagerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation {
    public TeleporterManagerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }
}
