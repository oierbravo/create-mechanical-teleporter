package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter;

import com.mojang.serialization.MapCodec;
import com.oierbravo.create_mechanical_teleporter.registrate.ModBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class TeleporterBlock extends HorizontalKineticBlock implements IBE<TeleporterBlockEntity>, IWrenchable {
    public static final MapCodec<PackagerLinkBlock> CODEC = simpleCodec(PackagerLinkBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;


    public TeleporterBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    public static int getPower(BlockState blockState, Level level, BlockPos worldPosition) {
       if(level.hasNeighborSignal(worldPosition)){
            return level.getBestNeighborSignal(worldPosition);
       }
        return 0;
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
    public Class<TeleporterBlockEntity> getBlockEntityClass() {
        return TeleporterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TeleporterBlockEntity> getBlockEntityType() {
        return ModBlockEntities.MECHANICAL_TELEPORTER.get();
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction prefferedSide = getPreferredHorizontalFacing(context);
        BlockPos pos = context.getClickedPos();
        BlockState placed = super.getStateForPlacement(context);

        if (prefferedSide == null)
            prefferedSide = context.getHorizontalDirection();
        return defaultBlockState().setValue(POWERED, getPower(placed, context.getLevel(), pos) > 0).setValue(HORIZONTAL_FACING, context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown() ? prefferedSide : prefferedSide.getOpposite());
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
                                boolean isMoving) {
        if (worldIn.isClientSide)
            return;

        int power = getPower(state, worldIn, pos);
        boolean powered = power > 0;
        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered != powered)
            worldIn.setBlock(pos, state.cycle(POWERED), 2);
        withBlockEntityDo(worldIn, pos, link -> link.teleporterBehavior.redstonePowerChanged(power));
    }




    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.FAST;
    }

    /*@Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            withBlockEntityDo(worldIn, pos, TeleporterBlockEntity::setRemoved);

            worldIn.removeBlockEntity(pos);
        }
    }*/
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        withBlockEntityDo(pLevel, pPos, tbe -> {
            if (pPlacer instanceof Player player) {
                tbe.placedBy = player.getUUID();
                tbe.notifyUpdate();
            }
        });
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POWERED));
    }
    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }
}
