package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter;

import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.create_mechanical_teleporter.content.logistics.IHaveTeleportFrequency;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterBehavior;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import com.oierbravo.create_mechanical_teleporter.foundation.ChunkManager;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.List;
import java.util.UUID;

public class TeleporterBlockEntity extends KineticBlockEntity implements TeleporterBehavior.TeleporterBehaviourSpecifics, ITeleporterBlockEntity, IHaveTeleportFrequency {
    public UUID placedBy;

    public TeleporterBehavior teleporterBehavior;

    protected boolean isActive = false;

    @Override
    public void lazyTick() {
        assert level != null;
        if(level.isClientSide)
            return;
        boolean isCurrentlyActive = checkRequerimentsForTeleport();
        if(isActive != isCurrentlyActive)
            setActive(isCurrentlyActive);
    }

    public TeleporterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        setLazyTickRate(10);
        placedBy = null;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (placedBy != null)
            tag.putUUID("PlacedBy", placedBy);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        placedBy = tag.contains("PlacedBy") ? tag.getUUID("PlacedBy") : null;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(teleporterBehavior = new TeleporterBehavior(this, true));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateCapabilities();
    }
    @Override
    public void remove() {
        if(MConfigs.server().teleporter.autoChunkLoad.get())
            ChunkManager.unLoadForcedChunks(this.level, this.getBlockPos());
    }

    public boolean checkRequerimentsForTeleport(){
        if(isPowered())
            return false;
        if(this.overStressed){
            return false;
        }

        if(!this.isSpeedRequirementFulfilled()){
            return false;
        }
        return true;
    }


    @Override
    public void initialize() {
        super.initialize();
        teleporterBehavior.redstonePowerChanged(TeleporterBlock.getPower(getBlockState(), level, worldPosition));
        if(MConfigs.server().teleporter.autoChunkLoad.get())
            ChunkManager.loadForcedChunks(this.level, this.getBlockPos());
    }


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if(!MConfigs.server().teleporter.autoChunkLoad.get())
            return added;
        if(!isSpeedRequirementFulfilled())
            return added;

        containedFluidTooltip(tooltip, isPlayerSneaking,
                level.getCapability(Capabilities.FluidHandler.BLOCK, this.getBlockPos(), null));

        ModLang.chunkLoader_loaded.t().style(ChatFormatting.GREEN).forGoggles(tooltip);
        return true;
    }
    protected boolean isPowered(){
        return this.getBlockState().getProperties().contains(TeleporterBlock.POWERED) && this.getBlockState().getValue(TeleporterBlock.POWERED);
    }

    @Override
    public TeleporterBehavior getTeleporter() {
        return teleporterBehavior;
    }

    public void setActive(boolean value){
        isActive = value;
        BlockState pState = getBlockState().setValue(TeleporterBlock.ACTIVE, value);

        getLevel().setBlock(getBlockPos(), pState, 2);
        setChanged(getLevel(), getBlockPos(), pState);
    }

    @Override
    public TeleporterFrequency getFrequency() {
        return TeleporterFrequency.from(teleporterBehavior);
    }
    @Override
    public UUID getPlacedBy() {
        return placedBy;
    }

    @Override
    public TeleporterBehavior.TELEPORTER_TYPES getTeleporterType() {
        return TeleporterBehavior.TELEPORTER_TYPES.MECHANICAL;
    }
}

