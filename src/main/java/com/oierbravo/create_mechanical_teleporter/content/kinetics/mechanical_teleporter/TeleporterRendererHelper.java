package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.oierbravo.create_mechanical_teleporter.registrate.ModPartials;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TeleporterRendererHelper {
    public static void renderCoreShared(PoseStack ms, @Nullable PoseStack modelTransform, MultiBufferSource bufferSource,
                                    Level level, BlockState blockState, boolean glow, int direction){
        VertexConsumer vb = bufferSource.getBuffer(RenderType.translucent());

        PartialModel coreModel = (glow) ? ModPartials.BLOCK_CORE_GLOW : ModPartials.BLOCK_CORE;

        SuperByteBuffer coreRenderer = CachedBuffers.partial(coreModel, blockState);

        float worldTime = AnimationTickHolder.getRenderTime() / 20;

        float floating = (glow) ? Mth.sin(worldTime) * .05f : 0;
        float angle = (glow)? direction * worldTime * -10 % 360 : 0;
        if (modelTransform != null)
            coreRenderer.transform(modelTransform);
        coreRenderer.translate(0, floating, 0);
        coreRenderer.rotateCentered(angle, Direction.UP)
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(ms, vb);

    }
    public static void renderCoreInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                  ContraptionMatrices matrices, MultiBufferSource bufferSource){
        renderCoreShared(matrices.getViewProjection(),matrices.getModel(),bufferSource, context.world, context.state, true, 1);
    }

    public static void renderBlockEntityOutline(){

    }
}
