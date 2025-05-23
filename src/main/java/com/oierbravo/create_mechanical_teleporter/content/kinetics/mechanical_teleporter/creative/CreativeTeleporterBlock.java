package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.creative;

import com.mojang.serialization.MapCodec;
import com.oierbravo.create_mechanical_teleporter.registrate.ModBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class CreativeTeleporterBlock extends Block implements IBE<CreativeTeleporterBlockEntity>, IWrenchable {
    public static final MapCodec<CreativeTeleporterBlock> CODEC = simpleCodec(CreativeTeleporterBlock::new);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;


    public CreativeTeleporterBlock(Properties properties) {
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState placed = super.getStateForPlacement(context);
        return defaultBlockState().setValue(POWERED, getPower(placed, context.getLevel(), pos) > 0);
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
        withBlockEntityDo(worldIn, pos, creativeTeleporterBlockEntity -> {
            creativeTeleporterBlockEntity.teleporterBehavior.redstonePowerChanged(power);
        });
    }

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

    @Override
    public Class<CreativeTeleporterBlockEntity> getBlockEntityClass() {
        return CreativeTeleporterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreativeTeleporterBlockEntity> getBlockEntityType() {
        return ModBlockEntities.CREATIVE_TELEPORTER.get();
    }
}
