package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.oierbravo.create_mechanical_teleporter.registrate.ModPartials;
import com.oierbravo.mechanicals.MechanicalPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class TeleporterRenderer extends KineticBlockEntityRenderer<TeleporterBlockEntity> {
    public TeleporterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TeleporterBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light,
                              int overlay) {

        BlockState blockState = blockEntity.getBlockState();

        VertexConsumer vb = buffer.getBuffer(RenderType.translucent());
        boolean canWork = blockEntity.checkRequerimentsForTeleport();
        PartialModel coreModel = (canWork) ? ModPartials.BLOCK_CORE_GLOW : ModPartials.BLOCK_CORE;

        SuperByteBuffer coreRenderer = CachedBuffers.partialFacing(coreModel, blockState,
            blockState.getValue(HORIZONTAL_FACING).getOpposite());

        float worldTime = AnimationTickHolder.getRenderTime() / 20;

        int direction = (blockEntity.getSpeed() >=0) ? 1 : -1;

        float floating = Mth.sin(worldTime) * .05f;
        float angle = direction * worldTime * -10 % 360;

        if(canWork)
            coreRenderer.translate(0, floating, 0);

        coreRenderer.rotateCentered((canWork) ? angle : 0, Direction.UP)
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(ms, vb);

        if (VisualizationManager.supportsVisualization(blockEntity.getLevel()))
            return;

        SuperByteBuffer shaftBuffer = CachedBuffers.partialFacingVertical(MechanicalPartials.SHAFT_QUARTER,blockState,Direction.WEST);
        standardKineticRotationTransform(shaftBuffer, blockEntity, light).renderInto(ms, vb);
    }
}
