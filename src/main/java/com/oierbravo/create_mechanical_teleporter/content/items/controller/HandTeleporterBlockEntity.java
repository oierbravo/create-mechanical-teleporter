package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.oierbravo.create_mechanical_teleporter.content.logistics.IHaveTeleportFrequency;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;

public class HandTeleporterBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation, IHaveTeleportFrequency {
    UUID freqId;
    String address = "";
    private Component customName;


    public HandTeleporterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public TeleporterFrequency getFrequency() {
        return TeleporterFrequency.from(freqId, address);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }
    public void setCustomName(Component customName) {
        this.customName = customName;
    }


    public CompoundTag getCustomData() {
        CompoundTag tag = new CompoundTag();
        if(freqId != null)
            tag.putUUID("Freq", freqId);
        tag.putString("Address", address);
        return tag;
    }

    public Component getCustomName() {
        return customName;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyUpdate();
    }

    @Override
    public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if(freqId != null)
            tag.putUUID("Freq", freqId);
        tag.putString("Address", address);
    }

    @Override
    public void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.hasUUID("Freq"))
            freqId = tag.getUUID("Freq");
        address = tag.getString("Address");
    }

    public void setFrequency(UUID uuid) {
        freqId = uuid;
        notifyUpdate();
    }
}
