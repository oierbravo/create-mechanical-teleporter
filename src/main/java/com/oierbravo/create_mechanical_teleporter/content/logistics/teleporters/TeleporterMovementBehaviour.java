package com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters;

import com.mojang.logging.LogUtils;
import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportersNetwork;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
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

import java.util.UUID;


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
        } else if (ce instanceof OrientedContraptionEntity o) {
            UUID freq = teleporterData.getUUID("Freq");
            TeleportersNetwork.ContraptionLink link = new TeleportersNetwork.ContraptionLink(
                    TeleportersNetwork.ContraptionKind.CART, o.level().dimension(), o.getUUID(), teleporterData.getString("SignAddress"));
            link.lastKnownPos = o.blockPosition();
            MechanicalTeleporter.TELEPORTERS.contraptionLinkAdded(freq, link, teleporterData.getUUID("PlacedBy"));
            context.temporaryData = new CartLinkData(freq, link);
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
        if (context.temporaryData instanceof CartLinkData cartLinkData)
            MechanicalTeleporter.TELEPORTERS.contraptionLinkRemoved(cartLinkData.freq, cartLinkData.link);
        context.temporaryData = null;
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world.isClientSide || !(context.world instanceof ServerLevel))
            return;
        // Self-heal: startMoving only fires once at assembly; chunk reload rebuilds the actor context
        // without calling it, so re-register the train link here. The link is removed on unload via
        // stopMoving and must be restored when the carriage loads back in.
        if (context.contraption.entity instanceof CarriageContraptionEntity carriageContraptionEntity) {
            CompoundTag teleporterData = context.blockEntityData;
            UUID freq = teleporterData.getUUID("Freq");
            String address = teleporterData.getString("SignAddress");
            if (!MechanicalTeleporter.TELEPORTERS.hasTrainLink(freq, carriageContraptionEntity.trainId, carriageContraptionEntity.carriageIndex, address))
                MechanicalTeleporter.TELEPORTERS.trainLinkAdded(freq, carriageContraptionEntity.trainId, carriageContraptionEntity.carriageIndex, address, teleporterData.getUUID("PlacedBy"));
        } else if (context.contraption.entity instanceof OrientedContraptionEntity o) {
            CompoundTag teleporterData = context.blockEntityData;
            UUID freq = teleporterData.getUUID("Freq");
            String address = teleporterData.getString("SignAddress");
            TeleportersNetwork.ContraptionLink link = new TeleportersNetwork.ContraptionLink(
                    TeleportersNetwork.ContraptionKind.CART, o.level().dimension(), o.getUUID(), address);
            link.lastKnownPos = o.blockPosition();
            if (!MechanicalTeleporter.TELEPORTERS.hasContraptionLink(freq, link))
                MechanicalTeleporter.TELEPORTERS.contraptionLinkAdded(freq, link, teleporterData.getUUID("PlacedBy"));
            context.temporaryData = new CartLinkData(freq, link);
            if (context.world.getGameTime() % 20 == 0)
                MechanicalTeleporter.TELEPORTERS.updateContraptionPos(freq, link, o.blockPosition());
        }
        MovementBehaviour.super.tick(context);
    }

    private record CartLinkData(UUID freq, TeleportersNetwork.ContraptionLink link) {
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
