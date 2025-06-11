package com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters.mechanical;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.oierbravo.mechanicals.MechanicalPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class TeleporterRenderer extends KineticBlockEntityRenderer<TeleporterBlockEntity> {
    public TeleporterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TeleporterBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light,
                              int overlay) {

        boolean isActive = blockEntity.checkRequerimentsForTeleport();

        int direction = (blockEntity.getSpeed() >=0) ? 1 : -1;
        TeleporterRendererHelper.renderCoreShared(ms,null,buffer, blockEntity.getLevel(),blockEntity.getBlockState(),isActive, direction);

        if (VisualizationManager.supportsVisualization(blockEntity.getLevel()))
            return;

        VertexConsumer vb = buffer.getBuffer(RenderType.translucent());
        SuperByteBuffer shaftBuffer = CachedBuffers.partialFacingVertical(MechanicalPartials.SHAFT_QUARTER,blockEntity.getBlockState(),Direction.WEST);
        standardKineticRotationTransform(shaftBuffer, blockEntity, light).renderInto(ms, vb);
    }
}
