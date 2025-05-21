package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class TeleporterRenderer extends KineticBlockEntityRenderer<TeleporterBlockEntity> {
    public TeleporterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TeleporterBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light,
                              int overlay) {
        //renderOnBlockEntity(blockEntity, partialTicks, ms, buffer, light, overlay);
    }




}
