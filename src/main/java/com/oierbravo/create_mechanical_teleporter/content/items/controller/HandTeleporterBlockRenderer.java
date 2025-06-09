package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.oierbravo.create_mechanical_teleporter.registrate.ModPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;


public class HandTeleporterBlockRenderer extends SmartBlockEntityRenderer<HandTeleporterBlockEntity> {

    public HandTeleporterBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(HandTeleporterBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        //super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        /*if (VisualizationManager.supportsVisualization(blockEntity.getLevel()))
            return;*/
        boolean active = blockEntity.freqId != null;
        VertexConsumer vb = buffer.getBuffer(RenderType.cutout());


        BlockState blockState = blockEntity.getBlockState();
        Direction facing = blockState.getValue(DirectionalBlock.FACING);

        PartialModel button = (active) ? ModPartials.BUTTON_ACTIVE : ModPartials.BUTTON;
        SuperByteBuffer buttonRenderer = CachedBuffers.partialFacingVertical(button,blockEntity.getBlockState(), Direction.SOUTH);
        buttonRenderer.center()
                .rotateYDegrees(AngleHelper.horizontalAngle(facing))
                .rotateXDegrees(AngleHelper.verticalAngle(facing))
                .rotateZDegrees(0)
                .uncenter();
        buttonRenderer.light(light).renderInto(ms, vb);


        PartialModel antenna = (active) ? ModPartials.ANTENNA_ACTIVE : ModPartials.ANTENNA;
        SuperByteBuffer antennaRenderer = CachedBuffers.partialFacingVertical(antenna,blockEntity.getBlockState(), Direction.SOUTH);
        antennaRenderer.center()
                .rotateYDegrees(AngleHelper.horizontalAngle(facing))
                .rotateXDegrees(AngleHelper.verticalAngle(facing))
                .rotateZDegrees(0)
                .uncenter();
        antennaRenderer.light(light).renderInto(ms, vb);

        if (blockEntity.getFrequency().address() != null && !blockEntity.getFrequency().address().isEmpty()) {
            renderNameplateOnHover(blockEntity, Component.literal(blockEntity.getFrequency().address()), 1, ms, buffer, light);
        }
    }
}
