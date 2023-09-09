package com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.ITeleportLinkable;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportLinkNetworkHandler;
import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Couple;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;
import java.util.function.Function;

public class TeleportLinkBehaviour extends BlockEntityBehaviour implements ITeleportLinkable{
    public static final BehaviourType<TeleportLinkBehaviour> TYPE = new BehaviourType<>();
    Vec3 textShift;

    UUID uuid;


    public boolean newPosition;
    private BooleanConsumer signalCallback;

    public TeleportLinkBehaviour(SmartBlockEntity te) {
        super(te);
        textShift = Vec3.ZERO;
        newPosition = true;
    }



    @Override
    public void doTeleport(ServerPlayer pPlayer) {
        if (!newPosition)
            return;
        if(this.blockEntity instanceof TeleporterBlockEntity tile){
            tile.doTeleport(pPlayer);
        }

    }

    @Override
    public UUID getUUID() {
        return null;
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
    public UUID getNetworkKey() {
        return uuid;
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


    public void setUUID(boolean uuid) {


        blockEntity.sendData();
        getHandler().addToNetwork(getWorld(), this);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    private TeleportLinkNetworkHandler getHandler() {
        return MechanicalTeleporter.TELEPORT_NETWORK_HANDLER;
    }

    public static class SlotPositioning {
        float scale;

        public SlotPositioning(Function<BlockState, Pair<Vec3, Vec3>> offsetsForState,
                               Function<BlockState, Vec3> rotationForState) {
            scale = 1;
        }

        public SlotPositioning scale(float scale) {
            this.scale = scale;
            return this;
        }

    }

   /* public boolean testHit(Boolean first, Vec3 hit) {
        BlockState state = blockEntity.getBlockState();
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(blockEntity.getBlockPos()));
        return (first ? firstSlot : secondSlot).testHit(state, localHit);
    }*/

    @Override
    public boolean isAlive() {
        return !blockEntity.isRemoved() && getWorld().getBlockEntity(getPos()) == blockEntity;
    }

    @Override
    public BlockPos getLocation() {
        return getPos();
    }


}
