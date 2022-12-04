package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport.TeleportLinkBehaviour;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.fluids.tank.FluidTankTileEntity;
import com.simibubi.create.foundation.block.BlockStressValues;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
public class TeleporterTile extends KineticTileEntity {
    //private final FluidTank fluidTankHandler = createFluidTank();
    protected FluidTank fluidTank;

    protected LazyOptional<IFluidHandler> fluidCapability;
    //private LazyOptional<IFluidHandler> outputFluidHandler = LazyOptional.of(() -> fluidTankHandler);

    private TeleportLinkBehaviour teleport;


    public TeleporterTile(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {

        super(typeIn, pos, state);
        fluidTank = createFluidTank();
        fluidCapability = LazyOptional.of(() -> fluidTank);
    }
    public static int FLUID_CAPACITY = 2000;
    public int FLUID_AMOUNT_NEEDED = 1000;
    public ResourceLocation FLUID = new ResourceLocation("minecraft/lava");
    private FluidTank createFluidTankOld() {

        return new FluidTank(FLUID_CAPACITY) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if(!level.isClientSide()) {
                    //ModMessages.sendToClients(new FluidStackSyncS2CPacket(this.fluid, worldPosition));
                }
            }

        };
    }
    protected SmartFluidTank createFluidTank() {
        return new SmartFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);
    }
    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasLevel())
            return;

        if (!level.isClientSide) {
            setChanged();
            sendData();
        }


    }
    public static int getCapacityMultiplier() {
        return FLUID_CAPACITY;
    }
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (isFluidHandlerCap(cap))
            return fluidCapability.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        createTeleport();
        behaviours.add(teleport);


    }


    protected void createTeleport() {
        Pair<ValueBoxTransform, ValueBoxTransform> slots =
                ValueBoxTransform.Dual.makeSlots(TeleportLinkFrequencySlot::new);
        teleport = new TeleportLinkBehaviour(this, slots);
    }



    public void doTeleport(ServerPlayer pPlayer) {


        if(checkRequerimentsForTeleport(pPlayer)){
            consumeFluid();
            BlockPos destination = this.getBlockPos().above();
            pPlayer.teleportTo(destination.getX() + 0.5,destination.getY()+ 0.5,destination.getZ()+ 0.5);
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer localPlayer = mc.player;
            localPlayer.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);

        }

    }

    public boolean checkRequerimentsForTeleport(ServerPlayer pPlayer){
        if(this.overStressed){
            pPlayer.displayClientMessage(Component.translatable("create_mechanical_teleporter.simple_teleport_controller.overstressed"),true);
            return false;
        }

        if(this.speed < IRotate.SpeedLevel.MEDIUM.getSpeedValue()){
            pPlayer.displayClientMessage(Component.translatable("create_mechanical_teleporter.simple_teleport_controller.not_fast_enough"),true);
            return false;
        }
        String fluidType = this.fluidTank.getFluid().getFluid().getFluidType().toString();
        if(fluidType == FLUID.toString()){
            pPlayer.displayClientMessage(Component.translatable("create_mechanical_teleporter.simple_teleport_controller.not_valid_fluid"),true);
            return false;
        }
        if(this.fluidTank.getFluidAmount() < FLUID_AMOUNT_NEEDED){
            pPlayer.displayClientMessage(Component.translatable("create_mechanical_teleporter.simple_teleport_controller.not_enough_fluid"),true);
            return false;
        }

        return true;
    }
    public void consumeFluid(){
        this.fluidTank.drain(FLUID_AMOUNT_NEEDED, IFluidHandler.FluidAction.EXECUTE);
    }

    public TeleportLinkBehaviour getTeleport() {
        return teleport;
    }
    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("TankContent", fluidTank.writeToNBT(new CompoundTag()));

    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        fluidTank.readFromNBT(compound.getCompound("TankContent"));

    }
    @Override
    public void setRemoved() {
        super.setRemoved();
        fluidCapability.invalidate();
    }
}
