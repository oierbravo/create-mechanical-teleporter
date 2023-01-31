package com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.ITeleportLinkable;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportLinkNetworkHandler;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterTile;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.tileEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.linked.LinkBehaviour;
import com.simibubi.create.foundation.utility.Couple;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

public class TeleportLinkBehaviour extends TileEntityBehaviour implements ITeleportLinkable{
    public static final BehaviourType<TeleportLinkBehaviour> TYPE = new BehaviourType<>();
    TeleportLinkNetworkHandler.Frequency frequencyFirst;
    TeleportLinkNetworkHandler.Frequency frequencyLast;
    ValueBoxTransform firstSlot;
    ValueBoxTransform secondSlot;
    Vec3 textShift;


    public boolean newPosition;
    private BooleanConsumer signalCallback;

    public TeleportLinkBehaviour(SmartTileEntity te, Pair<ValueBoxTransform, ValueBoxTransform> slots) {
        super(te);
        frequencyFirst = TeleportLinkNetworkHandler.Frequency.EMPTY;
        frequencyLast = TeleportLinkNetworkHandler.Frequency.EMPTY;
        firstSlot = slots.getLeft();
        secondSlot = slots.getRight();
        textShift = Vec3.ZERO;
        newPosition = true;
    }



    @Override
    public void doTeleport(ServerPlayer pPlayer) {
        if (!newPosition)
            return;
        if(this.tileEntity instanceof TeleporterTile tile){
            tile.doTeleport(pPlayer);
        }

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
    public void remove() {
        super.remove();
        if (getWorld().isClientSide)
            return;
        getHandler().removeFromNetwork(getWorld(), this);
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        nbt.put("TeleportFrequencyFirst", frequencyFirst.getStack()
                .save(new CompoundTag()));
        nbt.put("TeleportFrequencyLast", frequencyLast.getStack()
                .save(new CompoundTag()));
        nbt.putLong("TeleportLastKnownPosition", tileEntity.getBlockPos()
                .asLong());
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        long positionInTag = tileEntity.getBlockPos()
                .asLong();
        long positionKey = nbt.getLong("TeleportLastKnownPosition");
        newPosition = positionInTag != positionKey;

        super.read(nbt, clientPacket);
        frequencyFirst = TeleportLinkNetworkHandler.Frequency.of(ItemStack.of(nbt.getCompound("TeleportFrequencyFirst")));
        frequencyLast = TeleportLinkNetworkHandler.Frequency.of(ItemStack.of(nbt.getCompound("TeleportFrequencyLast")));
    }

    public void setFrequency(boolean first, ItemStack stack) {
        stack = stack.copy();
        stack.setCount(1);
        ItemStack toCompare = first ? frequencyFirst.getStack() : frequencyLast.getStack();
        boolean changed =
                !ItemStack.isSame(stack, toCompare) || !ItemStack.tagMatches(stack, toCompare);

        if (changed)
            getHandler().removeFromNetwork(getWorld(), this);

        if (first)
            frequencyFirst = TeleportLinkNetworkHandler.Frequency.of(stack);
        else
            frequencyLast = TeleportLinkNetworkHandler.Frequency.of(stack);

        if (!changed)
            return;

        tileEntity.sendData();
        getHandler().addToNetwork(getWorld(), this);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    private TeleportLinkNetworkHandler getHandler() {
        return MechanicalTeleporter.TELEPORT_LINK_NETWORK_HANDLER;
    }

    public static class SlotPositioning {
        Function<BlockState, Pair<Vec3, Vec3>> offsets;
        Function<BlockState, Vec3> rotation;
        float scale;

        public SlotPositioning(Function<BlockState, Pair<Vec3, Vec3>> offsetsForState,
                               Function<BlockState, Vec3> rotationForState) {
            offsets = offsetsForState;
            rotation = rotationForState;
            scale = 1;
        }

        public SlotPositioning scale(float scale) {
            this.scale = scale;
            return this;
        }

    }

    public boolean testHit(Boolean first, Vec3 hit) {
        BlockState state = tileEntity.getBlockState();
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(tileEntity.getBlockPos()));
        return (first ? firstSlot : secondSlot).testHit(state, localHit);
    }

    @Override
    public boolean isAlive() {
        return !tileEntity.isRemoved() && getWorld().getBlockEntity(getPos()) == tileEntity;
    }

    @Override
    public BlockPos getLocation() {
        return getPos();
    }


}
