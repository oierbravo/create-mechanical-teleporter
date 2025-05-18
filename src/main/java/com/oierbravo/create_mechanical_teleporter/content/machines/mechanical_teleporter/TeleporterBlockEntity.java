package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter;

import com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport.TeleportLinkBehaviour;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;
import java.util.Optional;

public class TeleporterBlockEntity extends KineticBlockEntity {
    //private final FluidTank fluidTankHandler = createFluidTank();
    protected FluidTank fluidTank;

    protected Optional<IFluidHandler> fluidCapability;
    //private LazyOptional<IFluidHandler> outputFluidHandler = LazyOptional.of(() -> fluidTankHandler);

    private TeleportLinkBehaviour teleport;

    public TeleporterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {

        super(typeIn, pos, state);
        //fluidTank = createFluidTank();
        //fluidCapability = LazyOptional.of(() -> fluidTank);
    }
    //public static int FLUID_CAPACITY = 2000;
    //public int FLUID_AMOUNT_NEEDED = 1000;
    public ResourceLocation FLUID = ResourceLocation.fromNamespaceAndPath("minecraft","lava");
    /*private FluidTank createFluidTankOld() {

        return new FluidTank(FLUID_CAPACITY) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if(!level.isClientSide()) {
                    //ModMessages.sendToClients(new FluidStackSyncS2CPacket(this.fluid, worldPosition));
                }
            }

        };
    }*/
    /*protected SmartFluidTank createFluidTank() {
        return new SmartFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);
    }*/
    /*protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasLevel())
            return;

        if (!level.isClientSide) {
            setChanged();
            sendData();
        }


    }*/
    /*public static int getCapacityMultiplier() {
        return FLUID_CAPACITY;
    }*/
    /*@Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (isFluidHandlerCap(cap))
            return fluidCapability.cast();
        return super.getCapability(cap, side);
    }*/

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        createTeleport();
        behaviours.add(teleport);
    }

    protected void createTeleport() {
         teleport = new TeleportLinkBehaviour(this);
    }



    public void doTeleport(ServerPlayer pPlayer) {


        if(checkRequerimentsForTeleport(pPlayer)){
            //consumeFluid();
            BlockPos destination = this.getBlockPos().above();
            pPlayer.teleportTo(destination.getX() + 0.5,destination.getY()+ 0.5,destination.getZ()+ 0.5);
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer localPlayer = mc.player;
            //try {
                localPlayer.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);

            //} catch ()

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
       /* if(this.fluidTank.getFluidAmount() < FLUID_AMOUNT_NEEDED){
            pPlayer.displayClientMessage(Component.translatable("create_mechanical_teleporter.simple_teleport_controller.not_enough_fluid"),true);
            return false;
        }*/

        return true;
    }
    /*public void consumeFluid(){
        this.fluidTank.drain(FLUID_AMOUNT_NEEDED, IFluidHandler.FluidAction.EXECUTE);
    }*/

    public TeleportLinkBehaviour getTeleport() {
        return teleport;
    }
    /*@Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("TankContent", fluidTank.writeToNBT(new CompoundTag()));

    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        fluidTank.readFromNBT(compound.getCompound("TankContent"));

    }*/



}
