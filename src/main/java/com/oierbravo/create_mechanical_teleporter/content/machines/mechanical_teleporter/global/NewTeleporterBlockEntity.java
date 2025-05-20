package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.global;

import com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport.TeleporterBehavior;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport.TeleportHandler.teleportToTeleporter;
import static com.simibubi.create.content.contraptions.actors.seat.SeatBlock.sitDown;

public class NewTeleporterBlockEntity extends KineticBlockEntity {
    //private final FluidTank fluidTankHandler = createFluidTank();

    public UUID placedBy;

    protected FluidTank fluidTank;

    protected Optional<IFluidHandler> fluidCapability;
    //private LazyOptional<IFluidHandler> outputFluidHandler = LazyOptional.of(() -> fluidTankHandler);
    public TeleporterBehavior teleporterBehavior;

    //private TeleportLinkBehaviour teleport;

    public NewTeleporterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {

        super(typeIn, pos, state);
        setLazyTickRate(10);
        placedBy = null;
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


    /*protected void createTeleport() {
        Pair<ValueBoxTransform, ValueBoxTransform> slots =
                ValueBoxTransform.Dual.makeSlots(TeleportLinkFrequencySlot::new);

         teleport = new TeleportLinkBehaviour(this, slots, new TeleporterModeSlot(), owner);
    }*/



    public void doTeleport(ServerPlayer pPlayer) {
        if(checkRequerimentsForTeleport(pPlayer)){
            //consumeFluid();
            Block blockAbove = this.level.getBlockState(this.getBlockPos().above()).getBlock();

            pPlayer.dismountTo(0.5,0.5,0.5);

            if(blockAbove instanceof SeatBlock seatBlock){
                sitDown(this.level,this.getBlockPos().above(), pPlayer);
            } else {
                teleportToTeleporter(this.level, pPlayer,this.getBlockPos());

            }


            //Minecraft mc = Minecraft.getInstance();
            //LocalPlayer localPlayer = mc.player;
            //try {
            //localPlayer.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);

            //} catch ()

        }

    }

    public boolean checkRequerimentsForTeleport(ServerPlayer pPlayer){
        if(this.overStressed){
            pPlayer.displayClientMessage(Component.translatable("create_mechanical_teleporter.simple_teleport_controller.overstressed"),true);
            return false;
        }

        /*if(this.speed < IRotate.SpeedLevel.MEDIUM.getSpeedValue()){
            pPlayer.displayClientMessage(Component.translatable("create_mechanical_teleporter.simple_teleport_controller.not_fast_enough"),true);
            return false;
        }*/
        /*String fluidType = this.fluidTank.getFluid().getFluid().getFluidType().toString();
        if(fluidType == FLUID.toString()){
            pPlayer.displayClientMessage(Component.translatable("create_mechanical_teleporter.simple_teleport_controller.not_valid_fluid"),true);
            return false;
        }*/
       /* if(this.fluidTank.getFluidAmount() < FLUID_AMOUNT_NEEDED){
            pPlayer.displayClientMessage(Component.translatable("create_mechanical_teleporter.simple_teleport_controller.not_enough_fluid"),true);
            return false;
        }*/

        return true;
    }
    /*public void consumeFluid(){
        this.fluidTank.drain(FLUID_AMOUNT_NEEDED, IFluidHandler.FluidAction.EXECUTE);
    }*/

    /*public TeleportLinkBehaviour getTeleport() {
        return teleport;
    }*/
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
    //ToDo: Chunkload?
    /*@Override
    public void tick() {
        super.tick();
        assert level != null;
        if (level.isClientSide) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) this.level;

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                serverLevel.setChunkForced(
                        new ChunkPos(getBlockPos()).x + i,
                        new ChunkPos(getBlockPos()).z + j,
                        Math.abs(this.getSpeed()) >= 16 * 8 * Math.max(Math.abs(i), Math.abs(j))
                );
            }
        }
    }*/

    @Override
    public void initialize() {
        super.initialize();
        teleporterBehavior.redstonePowerChanged(NewTeleporterBlock.getPower(getBlockState(), level, worldPosition));
    }
    public void setPlacedBy(UUID uuid){
        this.placedBy = uuid;
    }

}
