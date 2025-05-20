package com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.content.logistics.ITeleportLinkable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

//From enderio-machines/src/main/java/com/enderio/machines/client/rendering/travel/TravelAnchorRenderer.java
public class TeleportPointRenderer {
    public static final RenderType BOLD_LINES = OutlineRenderType.createLines("bold_lines", 3);
    public static final RenderType VERY_BOLD_LINES = OutlineRenderType.createLines("very_bold_lines", 5);

    public void render(ITeleportLinkable iTeleportLinkable, PoseStack poseStack,
                       double distanceSquared, boolean active, float partialTick) {
        /*if (!travelData.isVisible()) {
            return;
        }*/

        poseStack.pushPose();
        poseStack.translate(iTeleportLinkable.getBlockPos().getX(), iTeleportLinkable.getBlockPos().getY(), iTeleportLinkable.getBlockPos().getZ());
        Minecraft minecraft = Minecraft.getInstance();
        OutlineBuffer buffer = OutlineBuffer.INSTANCE;
        int color = 0xFFFFFF;
        if (active) {
            color = ChatFormatting.GOLD.getColor() == null ? 0xFFFFFF : ChatFormatting.GOLD.getColor();
        }

        // Render Model
        BlockState blockState = minecraft.level.getBlockState(iTeleportLinkable.getBlockPos());

        BakedModel blockModel = minecraft.getBlockRenderer().getBlockModel(blockState);
        VertexConsumer solid = buffer.getBuffer(RenderType.solid());
        minecraft.getBlockRenderer()
                .getModelRenderer()
                .renderModel(poseStack.last(), solid, blockState, blockModel, 1, 1, 1, 0xF000F0,
                        OverlayTexture.NO_OVERLAY);

        // Render line
        RenderType lineType;
        if (distanceSquared > 85 * 85) {
            lineType = RenderType.lines();
        } else if (distanceSquared > 38 * 38) {
            lineType = BOLD_LINES;
        } else {
            lineType = VERY_BOLD_LINES;
        }
        VertexConsumer lines = buffer.getBuffer(lineType);
        LevelRenderer.renderLineBox(poseStack, lines, 0, 0, 0, 1, 1, 1, FastColor.ARGB32.red(color) / 255F,
                FastColor.ARGB32.green(color) / 255F, FastColor.ARGB32.blue(color) / 255F, 1);

        LocalPlayer player = Minecraft.getInstance().player;

        Vec2 playerLookRotation = player.getRotationVector();
        Vec3 playerEyePosition = player.getEyePosition(partialTick);
        Vec3 playerOffset = iTeleportLinkable.getBlockPos().getCenter().vectorTo(playerEyePosition);
        Vec3 playerOffsetNormalized = playerOffset.normalize();

        // Render Text
        if (!iTeleportLinkable.name().trim().isEmpty()) {
            // Scale for rendering
            double doubleScale = Math.sqrt(0.0035 * Math.sqrt(distanceSquared));
            if (doubleScale < 0.1f) {
                doubleScale = 0.1f;
            }
            doubleScale = doubleScale * (Math.sin(Math.toRadians(Minecraft.getInstance().options.fov().get() / 4d)));
            if (active) {
                doubleScale *= 1.3;
            }
            float scale = (float) doubleScale;

            Quaternionf textRotation = Axis.YN.rotationDegrees(playerLookRotation.y)
                    .mul(Axis.XP.rotationDegrees(playerLookRotation.x));
            Vec3 offset = playerOffsetNormalized.scale(1.25);

            poseStack.pushPose();
            poseStack.translate(offset.x() + 0.5,
                    offset.y() + 1.05 + (doubleScale * Minecraft.getInstance().font.lineHeight), offset.z() + 0.5);
            poseStack.mulPose(textRotation);
            poseStack.scale(-scale, -scale, scale);

            Matrix4f matrix4f = poseStack.last().pose();
            Component tc = Component.literal(iTeleportLinkable.name().trim());

            float textOpacitySetting = minecraft.options.getBackgroundOpacity(0.5f);
            int alpha = (int) (textOpacitySetting * 255) << 24;
            float halfWidth = (float) (-minecraft.font.width(tc) / 2);

            minecraft.font.drawInBatch(tc, halfWidth, 0, color, false, matrix4f, buffer, Font.DisplayMode.SEE_THROUGH,
                    alpha, LightTexture.pack(15, 15));
            minecraft.font.drawInBatch(tc, halfWidth, 0, color, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0,
                    LightTexture.pack(15, 15));
            poseStack.popPose();
        }

        // Render Icon
        /*if (travelData.icon() != Items.AIR) {
            // Scale for rendering
            double doubleScale = Math.sqrt(Math.sqrt(distanceSquared));
            doubleScale = doubleScale * (Math.sin(Math.toRadians(Minecraft.getInstance().options.fov().get() / 4d)));
            if (active) {
                doubleScale *= 1.3;
            }
            float scale = (float) doubleScale;

            Vector3f direction = playerOffsetNormalized.toVector3f();
            Quaternionf iconRotation = new Quaternionf().lookAlong(direction.x(), direction.y(), direction.z(), 0F, 1F,
                    0F);
            Vec3 offset = playerOffsetNormalized.scale(0.9);

            poseStack.pushPose();
            poseStack.translate(offset.x() + 0.5, offset.y() + 0.5, offset.z() + 0.5);
            poseStack.mulPose(iconRotation.invert());
            poseStack.scale(-scale, scale, -scale);

            ItemStack stack = new ItemStack(travelData.icon());
            BakedModel bakedmodel = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 0);
            minecraft.getItemRenderer()
                    .render(stack, ItemDisplayContext.GUI, true, poseStack, OutlineBuffer.INSTANCE, 15728880,
                            OverlayTexture.NO_OVERLAY, bakedmodel);
            poseStack.popPose();
        }*/

        poseStack.popPose();
        minecraft.renderBuffers().bufferSource().endBatch();

    }
    public static class OutlineRenderType extends RenderType {

        private static final Map<RenderType, OutlineRenderType> TYPES = new HashMap<>();

        private final RenderType parent;

        private OutlineRenderType(RenderType parent) {
            super("Outline" + parent.name, parent.format(), parent.mode(), parent.bufferSize(), parent.affectsCrumbling(),
                    parent.sortOnUpload, parent::setupRenderState, parent::clearRenderState);
            this.parent = parent;
        }

        public static RenderType get(RenderType parent) {
            if (parent.name.contains("glint")) {
                return parent;
            } else if (parent instanceof OutlineRenderType) {
                return parent;
            } else {
                if (!TYPES.containsKey(parent)) {
                    TYPES.put(parent, new OutlineRenderType(parent));
                }
                return TYPES.get(parent);
            }
        }

        @NotNull
        @Override
        public String toString() {
            return "Outline" + this.parent;
        }

        @Override
        public void setupRenderState() {
            this.parent.setupRenderState();
            if (Minecraft.getInstance().levelRenderer.entityTarget() != null) {
                // noinspection ConstantConditions
                Minecraft.getInstance().levelRenderer.entityTarget().bindWrite(false);
            }
        }

        @Override
        public void clearRenderState() {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            this.parent.clearRenderState();
        }

        public static RenderType createLines(String name, int strength) {
            return RenderType.create(ModConstants.MODID + "_" + name, DefaultVertexFormat.POSITION_COLOR_NORMAL,
                    VertexFormat.Mode.LINES, 256, false, false,
                    CompositeState.builder()
                            .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                            .setLineState(new LineStateShard(OptionalDouble.of(strength)))
                            .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                            .setCullState(RenderStateShard.NO_CULL)
                            .createCompositeState(false));
        }
    }
    public static class OutlineBuffer implements MultiBufferSource {

        public static final OutlineBuffer INSTANCE = new OutlineBuffer();

        private OutlineBuffer() {}

        @NotNull
        @Override
        public VertexConsumer getBuffer(@NotNull RenderType type) {
            return Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(OutlineRenderType.get(type));
        }
    }
}
