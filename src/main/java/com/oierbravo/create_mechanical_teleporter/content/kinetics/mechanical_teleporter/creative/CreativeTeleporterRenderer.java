package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.creative;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.oierbravo.create_mechanical_teleporter.registrate.ModPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
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

public class CreativeTeleporterRenderer extends SafeBlockEntityRenderer<CreativeTeleporterBlockEntity> {
    public CreativeTeleporterRenderer(BlockEntityRendererProvider.Context context) {}


    @Override
    protected void renderSafe(CreativeTeleporterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        VertexConsumer vb = buffer.getBuffer(RenderType.translucent());

        PartialModel coreModel = ModPartials.BLOCK_CORE_GLOW;

        SuperByteBuffer coreRenderer = CachedBuffers.partial(coreModel, be.getBlockState());

        float worldTime = AnimationTickHolder.getRenderTime() / 20;

        int direction = 1;

        float floating = Mth.sin(worldTime) * .05f;
        float angle = direction * worldTime * -10 % 360;

        coreRenderer.translate(0, floating, 0);

        coreRenderer.rotateCentered(angle, Direction.UP)
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(ms, vb);
    }
}
