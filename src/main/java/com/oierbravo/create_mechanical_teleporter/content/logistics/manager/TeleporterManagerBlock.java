package com.oierbravo.create_mechanical_teleporter.content.logistics.manager;

import com.oierbravo.create_mechanical_teleporter.registrate.ModBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TeleporterManagerBlock extends Block implements IBE<TeleporterManagerBlockEntity>, IWrenchable {

    public TeleporterManagerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public Class<TeleporterManagerBlockEntity> getBlockEntityClass() {
        return TeleporterManagerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TeleporterManagerBlockEntity> getBlockEntityType() {
        return ModBlockEntities.TELEPORTER_MANAGER.get();
    }
}
