package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.oierbravo.create_mechanical_teleporter.infrastructure.network.RequestTeleportToFrequencyPayload;
import com.oierbravo.create_mechanical_teleporter.registrate.*;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.Optional;

public class HandTeleporterBlock extends WrenchableDirectionalBlock implements IBE<HandTeleporterBlockEntity> {
    protected final DyeColor color;

    public HandTeleporterBlock(Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockPos neighbourPos = pos.relative(state.getValue(FACING)
                .getOpposite());
        BlockState neighbour = worldIn.getBlockState(neighbourPos);
        return !neighbour.canBeReplaced();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        state = state.setValue(FACING, context.getClickedFace());
        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return ModShapes.HAND_TELEPORTER.get(state.getValue(FACING));
    }
    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
    @Override
    public Class<HandTeleporterBlockEntity> getBlockEntityClass() {
        return HandTeleporterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HandTeleporterBlockEntity> getBlockEntityType() {
        return ModBlockEntities.HAND_TELEPORTER.get();
    }
    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isClientSide)
            return;
        if (stack == null)
            return;
        withBlockEntityDo(worldIn, pos, be -> {
            if (stack.has(ModDataComponents.TELEPORTER_ADDRESS))
                be.setAddress(
                        stack.get(ModDataComponents.TELEPORTER_ADDRESS));
            if (stack.has(DataComponents.CUSTOM_NAME))
                be.setCustomName(stack.getHoverName());
        });
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moving) {
        if (state.hasBlockEntity() && (!newState.hasBlockEntity() || !(newState.getBlock() instanceof HandTeleporterBlock)))
            world.removeBlockEntity(pos);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack item = new ItemStack(this);
        Optional<HandTeleporterBlockEntity> blockEntityOptional = getBlockEntityOptional(level, pos);

        blockEntityOptional.map(HandTeleporterBlockEntity::getFrequency)
                .ifPresent(data -> {
                    item.set(ModDataComponents.TELEPORTER_FREQUENCY, data.freqId());
                    item.set(ModDataComponents.TELEPORTER_ADDRESS, data.address());
                });

        blockEntityOptional.map(HandTeleporterBlockEntity::getCustomName)
                .ifPresent(name -> item.set(DataComponents.CUSTOM_NAME, name));
        return item;
    }
    private void breakAndCollect(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        if (pPlayer instanceof FakePlayer)
            return;
        if (pLevel.isClientSide)
            return;
        ItemStack cloneItemStack = getCloneItemStack(pLevel, pPos, pState);
        pLevel.destroyBlock(pPos, false);
        if (pLevel.getBlockState(pPos) != pState)
            pPlayer.getInventory()
                    .placeItemBackInInventory(cloneItemStack);
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            breakAndCollect(state, level, pos, player);
            return InteractionResult.SUCCESS;
        }

        return onBlockEntityUse(level, pos, cbe -> {
            if (level.isClientSide())
                CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> ScreenOpener.open(new HandTeleporterScreen(cbe.getBlockPos(),cbe.address)));
            return InteractionResult.SUCCESS;
        });
    }
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player == null || player.isCrouching())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        DyeColor color = DyeColor.getColor(stack);
        if (color != null && color != this.color) {
            if (level.isClientSide)
                return ItemInteractionResult.SUCCESS;
            BlockState newState = BlockHelper.copyProperties(state, ModBlocks.HAND_TELEPORTERS.get(color)
                    .getDefaultState());
            level.setBlockAndUpdate(pos, newState);
            return ItemInteractionResult.SUCCESS;
        }

        if (player instanceof FakePlayer)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        if(player.isShiftKeyDown()){
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        withBlockEntityDo(level, pos,
                handTeleporter -> {
                    if(handTeleporter.freqId != null)
                        ModMessages.sendToServer(new RequestTeleportToFrequencyPayload(handTeleporter.getFrequency()));
                });

        return ItemInteractionResult.SUCCESS;
    }
    public DyeColor getColor() {
        return color;
    }

}
