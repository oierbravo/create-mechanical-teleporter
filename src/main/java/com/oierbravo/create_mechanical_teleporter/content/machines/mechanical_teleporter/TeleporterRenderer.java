package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport.TeleportLinkRenderer;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class TeleporterRenderer extends KineticTileEntityRenderer {
    public TeleporterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    public boolean shouldRenderOffScreen(KineticTileEntity te) {
        return true;
    }
    @Override
    protected void renderSafe(KineticTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        TeleportLinkRenderer.renderOnTileEntity(te, partialTicks, ms, buffer, light, overlay);

        super.renderSafe(te,partialTicks,ms,buffer,light,overlay);
        if (Backend.canUseInstancing(te.getLevel()))
            return;


    }

}
