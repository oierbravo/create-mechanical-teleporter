package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.creative;

import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.ITeleporterBlockEntity;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterBehavior;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterBlock;
import com.oierbravo.create_mechanical_teleporter.foundation.ChunkManager;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;

public class CreativeTeleporterBlockEntityBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation, TeleporterBehavior.TeleporterBehaviourSpecifics, ITeleporterBlockEntity {

    public TeleporterBehavior teleporterBehavior;

    public UUID placedBy;


    public CreativeTeleporterBlockEntityBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(teleporterBehavior = new TeleporterBehavior(this, true));
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
    public void remove() {
        if(MConfigs.server().teleporter.autoChunkLoad.get())
            ChunkManager.unLoadForcedChunks(this.level, this.getBlockPos());
    }

    public boolean checkRequerimentsForTeleport(){
        if(isPowered())
            return false;
        return true;
    }
    public void consumeFluid(){}

    @Override
    public void initialize() {
        super.initialize();
        teleporterBehavior.redstonePowerChanged(TeleporterBlock.getPower(getBlockState(), level, worldPosition));
        if(MConfigs.server().teleporter.autoChunkLoad.get())
            ChunkManager.loadForcedChunks(this.level, this.getBlockPos());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if(!MConfigs.server().teleporter.autoChunkLoad.get())
            return false;

        ModLang.translate("chunk_loader.loaded").style(ChatFormatting.GREEN).forGoggles(tooltip);
        return true;
    }
    protected boolean isPowered(){
        return this.getBlockState().getProperties().contains(TeleporterBlock.POWERED) && this.getBlockState().getValue(TeleporterBlock.POWERED);
    }

    @Override
    public TeleporterBehavior getTeleporter() {
        return teleporterBehavior;
    }
}
