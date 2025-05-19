package com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.ITeleportLinkable;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportLinkNetworkHandler;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterBlockEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeleportLinkBehaviour extends BlockEntityBehaviour implements ITeleportLinkable, ClipboardCloneable  {
    public static final BehaviourType<TeleportLinkBehaviour> TYPE = new BehaviourType<>();

    public TeleportLinkNetworkHandler.Frequency frequencyFirst;
    public TeleportLinkNetworkHandler.Frequency frequencyLast;
    public boolean isPrivate;
    public ValueBoxTransform firstSlot;
    public ValueBoxTransform secondSlot;
    public ValueBoxTransform isPrivateSlot;
    Vec3 textShift;

    UUID uuid;

    public boolean newPosition;

    public TeleportLinkBehaviour(SmartBlockEntity be, Pair<ValueBoxTransform, ValueBoxTransform> frequencySlots, ValueBoxTransform isPrivateSlot, UUID uuid) {
        super(be);
        frequencyFirst = TeleportLinkNetworkHandler.Frequency.EMPTY;
        frequencyLast = TeleportLinkNetworkHandler.Frequency.EMPTY;
        isPrivate = false;
        firstSlot = frequencySlots.getLeft();
        secondSlot = frequencySlots.getRight();
        isPrivateSlot = isPrivateSlot;
        textShift = Vec3.ZERO;
        newPosition = true;
        uuid = uuid;
    }

    public TeleportLinkBehaviour moveText(Vec3 shift) {
        textShift = shift;
        return this;
    }

    public void copyItemsFrom(TeleportLinkBehaviour behaviour) {
        if (behaviour == null)
            return;
        frequencyFirst = behaviour.frequencyFirst;
        frequencyLast = behaviour.frequencyLast;
        isPrivate = behaviour.isPrivate;
    }


    @Override
    public void doTeleport(ServerPlayer pPlayer) {
        if (!newPosition)
            return;
        if(this.blockEntity instanceof TeleporterBlockEntity teleporterBlockEntity){
            teleporterBlockEntity.doTeleport(pPlayer);
        }

    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public BlockPos getBlockPos() {
        return blockEntity.getBlockPos();
    }

    @Override
    public Level getLevel() {
        return blockEntity.getLevel();
    }

    @Override
    public boolean isRemoved() {
        return blockEntity.isRemoved();
    }

    @Override
    public String name() {
        return "";
    }


    @Override
    public void initialize() {
        super.initialize();
        if (getWorld().isClientSide)
            return;
        getHandler().addToNetwork(getWorld(), this);
        newPosition = true;
    }

    @Override
    public Couple<TeleportLinkNetworkHandler.Frequency> getNetworkKey() {
        return Couple.create(frequencyFirst, frequencyLast);
    }


    @Override
    public void unload() {
        super.unload();
        if (getWorld().isClientSide)
            return;
        getHandler().removeFromNetwork(getWorld(), this);
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    private TeleportLinkNetworkHandler getHandler() {
        return MechanicalTeleporter.TELEPORT_NETWORK_HANDLER;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        nbt.putBoolean("Private", isPrivate);
        nbt.put("FrequencyFirst", frequencyFirst.getStack()
                .saveOptional(registries));
        nbt.put("FrequencyLast", frequencyLast.getStack()
                .saveOptional(registries));
        nbt.putLong("LastKnownPosition", blockEntity.getBlockPos()
                .asLong());
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        isPrivate = nbt.getBoolean("Private");
        long positionInTag = blockEntity.getBlockPos()
                .asLong();
        long positionKey = nbt.getLong("LastKnownPosition");
        newPosition = positionInTag != positionKey;

        super.read(nbt, registries, clientPacket);
        frequencyFirst = TeleportLinkNetworkHandler.Frequency.of(ItemStack.parseOptional(registries, nbt.getCompound("FrequencyFirst")));
        frequencyLast = TeleportLinkNetworkHandler.Frequency.of(ItemStack.parseOptional(registries, nbt.getCompound("FrequencyLast")));
    }

    public void setFrequency(boolean first, ItemStack stack) {
        stack = stack.copy();
        stack.setCount(1);
        ItemStack toCompare = first ? frequencyFirst.getStack() : frequencyLast.getStack();
        boolean changed = !ItemStack.isSameItemSameComponents(stack, toCompare);

        if (changed)
            getHandler().removeFromNetwork(getWorld(), this);

        if (first)
            frequencyFirst = TeleportLinkNetworkHandler.Frequency.of(stack);
        else
            frequencyLast = TeleportLinkNetworkHandler.Frequency.of(stack);

        if (!changed)
            return;

        blockEntity.sendData();
        getHandler().addToNetwork(getWorld(), this);
    }


    /*public static class FrequencySlotPositioning extends ValueBoxTransform.Sided {
        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction != Direction.UP  && direction != Direction.DOWN;
        }
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8f, 8f,  16f);
        }

    }*/
    public boolean testHit(Boolean first, Vec3 hit) {
        BlockState state = blockEntity.getBlockState();
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(blockEntity.getBlockPos()));
        return (first ? firstSlot : secondSlot).testHit(getWorld(), getPos(), state, localHit);
    }

    @Override
    public boolean isAlive() {
        Level level = getWorld();
        BlockPos pos = getPos();
        if (blockEntity.isChunkUnloaded())
            return false;
        if (blockEntity.isRemoved())
            return false;
        if (!level.isLoaded(pos))
            return false;
        return level.getBlockEntity(pos) == blockEntity;
    }

    @Override
    public BlockPos getLocation() {
        return getPos();
    }

    @Override
    public String getClipboardKey() {
        return "Frequencies";
    }

    @Override
    public boolean writeToClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        tag.putBoolean("Private", isPrivate);
        tag.put("First", frequencyFirst.getStack()
                .saveOptional(registries));
        tag.put("Last", frequencyLast.getStack()
                .saveOptional(registries));
        return true;
    }

    @Override
    public boolean readFromClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (!tag.contains("First") || !tag.contains("Last") || !tag.contains("Private"))
            return false;
        if (simulate)
            return true;
        setIsPrivate(tag.getBoolean("Private"));
        setFrequency(true, ItemStack.parseOptional(registries, tag.getCompound("First")));
        setFrequency(false, ItemStack.parseOptional(registries, tag.getCompound("Last")));
        return true;
    }

    private void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
