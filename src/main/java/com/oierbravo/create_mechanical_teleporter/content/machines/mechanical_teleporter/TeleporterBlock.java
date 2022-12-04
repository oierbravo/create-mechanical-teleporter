package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter;

import com.oierbravo.create_mechanical_teleporter.registrate.ModTiles;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.content.contraptions.base.KineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class TeleporterBlock extends HorizontalKineticBlock implements ITE<TeleporterTile> {
    public TeleporterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }


    @Override
    public Class<TeleporterTile> getTileEntityClass() {
        return TeleporterTile.class;
    }

    @Override
    public BlockEntityType<? extends TeleporterTile> getTileEntityType() {
        return ModTiles.MECHANICAL_TELEPORTER.get();
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction prefferedSide = getPreferredHorizontalFacing(context);
        if (prefferedSide == null)
            prefferedSide = context.getHorizontalDirection();
        return defaultBlockState().setValue(HORIZONTAL_FACING, context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown() ? prefferedSide : prefferedSide.getOpposite());
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.FAST;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            withTileEntityDo(worldIn, pos, TeleporterTile::setRemoved);

            worldIn.removeBlockEntity(pos);
        }
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }


}
