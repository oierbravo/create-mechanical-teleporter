package com.oierbravo.create_mechanical_teleporter.content.fluid;

import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportHandler;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class EnderLiquidBlock extends LiquidBlock {
    public EnderLiquidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void entityInside(BlockState pState, Level level, BlockPos pPos, Entity entity) {
        if (!level.isClientSide) {
            //random teleport
            TeleportHandler.shortRandomTeleport(level, entity, MConfigs.server().misc.enderFluidTeleportRange.get());

        }
    }
}
