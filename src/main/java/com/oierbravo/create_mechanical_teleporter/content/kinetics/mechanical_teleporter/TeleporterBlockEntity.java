package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter;

import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.create_mechanical_teleporter.foundation.ChunkManager;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.oierbravo.create_mechanical_teleporter.registrate.ModBlockEntities;
import com.oierbravo.create_mechanical_teleporter.registrate.ModFluids;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.UUID;

public class TeleporterBlockEntity extends KineticBlockEntity implements TeleporterBehavior.TeleporterBehaviourSpecifics, ITeleporterBlockEntity {
    public UUID placedBy;

    public TeleporterBehavior teleporterBehavior;
    public SmartFluidTankBehaviour inputTank;

    public TeleporterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        setLazyTickRate(10);
        placedBy = null;
    }
    public static int FLUID_CAPACITY = 4000;

    public static Fluid REQUIRED_FLUID = ModFluids.ENDER_FLUID.get();

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
        inputTank = SmartFluidTankBehaviour.single(this, FLUID_CAPACITY);
        inputTank.getPrimaryHandler().setValidator(FluidIngredient.fromFluid(REQUIRED_FLUID,1000));

        behaviours.add(inputTank);
        behaviours.add(teleporterBehavior = new TeleporterBehavior(this, true));
    }
    @Override
    public void invalidate() {
        super.invalidate();
        invalidateCapabilities();
    }
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.MECHANICAL_TELEPORTER.get(),
                (be, context) -> {
                    Direction localDir = be.getBlockState().getValue(TeleporterBlock.HORIZONTAL_FACING);
                    if(context != null && localDir == context)
                        return be.inputTank.getPrimaryHandler();
                    if(context == null)
                        return be.inputTank.getPrimaryHandler();
                    return null;
                }
        );
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
        if(this.inputTank.getPrimaryHandler().getFluidAmount() < MConfigs.server().teleporter.requiredFluidAmount.get()){
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

        ModLang.translate("chunk_loader.loaded").style(ChatFormatting.GREEN).forGoggles(tooltip);
        return true;
    }
    protected boolean isPowered(){
        return this.getBlockState().getProperties().contains(TeleporterBlock.POWERED) && this.getBlockState().getValue(TeleporterBlock.POWERED);
    }

    @Override
    public void consumeResources() {
        this.inputTank.getPrimaryHandler().drain(MConfigs.server().teleporter.requiredFluidAmount.get(), IFluidHandler.FluidAction.EXECUTE);
    }
    @Override
    public TeleporterBehavior getTeleporter() {
        return teleporterBehavior;
    }
}

