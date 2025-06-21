package com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters;

import com.mojang.logging.LogUtils;
import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.slf4j.Logger;


public class TeleporterMovementBehaviour implements MovementBehaviour {
    private static final Logger LOGGER = LogUtils.getLogger();

    public TeleporterMovementBehaviour(){

    }

    @Override
    public boolean isActive(MovementContext context) {
        boolean a = MovementBehaviour.super.isActive(context);
        return MovementBehaviour.super.isActive(context);
    }

    @Override
    public void startMoving(MovementContext context) {
        if (context.world.isClientSide || !(context.world instanceof ServerLevel))
            return;
        if (context.contraption.entity == null)
            return;

        CompoundTag teleporterData = context.blockEntityData;
        AbstractContraptionEntity ce = context.contraption.entity;
        if(ce instanceof CarriageContraptionEntity carriageContraptionEntity){
            MechanicalTeleporter.TELEPORTERS.trainLinkAdded(teleporterData.getUUID("Freq"),carriageContraptionEntity.trainId, carriageContraptionEntity.carriageIndex, teleporterData.getString("SignAddress"), teleporterData.getUUID("PlacedBy"));
        }
    }

    @Override
    public void cancelStall(MovementContext context) {
        MovementBehaviour.super.cancelStall(context);
    }

    @Override
    public boolean mustTickWhileDisabled() {
        return true;
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        MovementBehaviour.super.visitNewPosition(context, pos);
    }

    @Override
    public void stopMoving(MovementContext context) {
        if (context.world.isClientSide || !(context.world instanceof ServerLevel))
            return;
        if (context.contraption.entity == null)
            return;

        CompoundTag teleporterData = context.blockEntityData;
        boolean dis = context.contraption.disassembled;
        boolean a = context.contraption.entity.blocksBuilding;
        if(context.contraption.entity instanceof CarriageContraptionEntity carriageContraptionEntity){
            if(!context.firstMovement)
                MechanicalTeleporter.TELEPORTERS.trainLinkRemoved(teleporterData.getUUID("Freq"),carriageContraptionEntity.trainId,carriageContraptionEntity.carriageIndex, teleporterData.getString("SignAddress"));
        }
        context.temporaryData = null;
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world.isClientSide || !(context.world instanceof ServerLevel))
            return;
        MovementBehaviour.super.tick(context);
    }



    @Override
    public void writeExtraData(MovementContext context) {
        MovementBehaviour.super.writeExtraData(context);
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                    ContraptionMatrices matrices, MultiBufferSource buffer) {

        TeleporterRendererHelper.renderCoreInContraption(context, renderWorld, matrices, buffer);
    }
}
