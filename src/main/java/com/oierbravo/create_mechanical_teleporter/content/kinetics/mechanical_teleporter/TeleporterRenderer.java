package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter;

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

        //BlockState blockState = blockEntity.getBlockState();

        //VertexConsumer vb = buffer.getBuffer(RenderType.translucent());
        boolean canWork = blockEntity.checkRequerimentsForTeleport();
        /*PartialModel coreModel = (canWork) ? ModPartials.BLOCK_CORE_GLOW : ModPartials.BLOCK_CORE;

        SuperByteBuffer coreRenderer = CachedBuffers.partialFacing(coreModel, blockState,
            blockState.getValue(HORIZONTAL_FACING).getOpposite());

        float worldTime = AnimationTickHolder.getRenderTime() / 20;



        float floating = Mth.sin(worldTime) * .05f;
        float angle = direction * worldTime * -10 % 360;

        if(canWork)
            coreRenderer.translate(0, floating, 0);

        coreRenderer.rotateCentered((canWork) ? angle : 0, Direction.UP)
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(ms, vb);*/

        int direction = (blockEntity.getSpeed() >=0) ? 1 : -1;
        TeleporterRendererHelper.renderCoreShared(ms,null,buffer, blockEntity.getLevel(),blockEntity.getBlockState(),canWork, direction);

        if (VisualizationManager.supportsVisualization(blockEntity.getLevel()))
            return;

        VertexConsumer vb = buffer.getBuffer(RenderType.translucent());
        SuperByteBuffer shaftBuffer = CachedBuffers.partialFacingVertical(MechanicalPartials.SHAFT_QUARTER,blockEntity.getBlockState(),Direction.WEST);
        standardKineticRotationTransform(shaftBuffer, blockEntity, light).renderInto(ms, vb);
    }
}
