package com.oierbravo.create_mechanical_teleporter.content.logistics.manager;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.create_mechanical_teleporter.content.logistics.IHaveTeleportFrequency;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterBehavior;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters.ITeleporterBlockEntity;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;

public class TeleporterManagerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation, MenuProvider, IHaveTeleportFrequency, TeleporterBehavior.TeleporterBehaviourSpecifics, ITeleporterBlockEntity {

    public UUID placedBy;
    public TeleporterBehavior teleporterBehavior;

    private Component customName;

    public TeleporterManagerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        placedBy = null;

    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(teleporterBehavior = new TeleporterBehavior(this, true));

    }
    private GlobalPos getGlobalPos() {
        assert getLevel() != null;
        return GlobalPos.of(getLevel().dimension(), getBlockPos());
    }

    public boolean checkRequerimentsForTeleport(){
        return false;
    }

    @Override
    public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (placedBy != null)
            tag.putUUID("PlacedBy", placedBy);
    }

    @Override
    public void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        placedBy = tag.contains("PlacedBy") ? tag.getUUID("PlacedBy") : null;
    }

    @Override
    public Component getDisplayName() {
        return ModLang.teleporterManager.title.t().component();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return TeleporterManagerMenu.create(id, inv, this);
    }

    @Override
    public TeleporterBehavior getTeleporter() {
        return teleporterBehavior;
    }
    @Override
    public TeleporterFrequency getFrequency() {
        return TeleporterFrequency.from(teleporterBehavior);
    }

    @Override
    public void sendToMenu(RegistryFriendlyByteBuf buffer) {
        TeleporterFrequency teleporterFrequency = TeleporterFrequency.from(this);
        Player player = Minecraft.getInstance().player;
        boolean showLockOption = player != null && teleporterFrequency.mayAdministrate(player) && MechanicalTeleporter.TELEPORTERS.isLockable(teleporterFrequency.freqId());
        boolean isCurrentlyLocked = MechanicalTeleporter.TELEPORTERS.isLocked(teleporterFrequency.freqId());

        buffer.writeBoolean(showLockOption);
        buffer.writeBoolean(isCurrentlyLocked);
        super.sendToMenu(buffer);
    }
    @Override
    public UUID getPlacedBy() {
        return placedBy;
    }

    @Override
    public TeleporterBehavior.TELEPORTER_TYPES getTeleporterType() {
        return TeleporterBehavior.TELEPORTER_TYPES.MANAGER;
    }

    @Override
    public boolean isTeleportable() {
        return false;
    }
    public void setCustomName(Component customName) {
        this.customName = customName;
    }
    public Component getCustomName(){
        return this.customName;
    }
}
