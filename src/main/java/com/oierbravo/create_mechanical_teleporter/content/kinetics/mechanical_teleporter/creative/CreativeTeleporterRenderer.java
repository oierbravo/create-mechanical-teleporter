package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.creative;

import com.mojang.blaze3d.vertex.PoseStack;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterRendererHelper;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class CreativeTeleporterRenderer extends SafeBlockEntityRenderer<CreativeTeleporterBlockEntity> {
    public CreativeTeleporterRenderer(BlockEntityRendererProvider.Context context) {}


    @Override
    protected void renderSafe(CreativeTeleporterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        TeleporterRendererHelper.renderCoreShared(ms,null,buffer, be.getLevel(),be.getBlockState(), true, 1);
    }
}
