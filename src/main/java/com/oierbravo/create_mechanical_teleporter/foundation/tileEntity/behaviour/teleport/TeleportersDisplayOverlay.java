package com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport;

import com.mojang.blaze3d.vertex.PoseStack;
import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.ITeleportLinkable;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;

import java.util.Collections;
import java.util.List;

public class TeleportersDisplayOverlay implements LayeredDraw.Layer  {
    public static final TeleportersDisplayOverlay INSTANCE = new TeleportersDisplayOverlay();

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();

        LocalPlayer player = mc.player;
        if (player == null)
            return;
        //if (player.isCreative())
        //    return;
        if(mc.level == null)
            return;
        if(ModItems.TELEPORT_WAND.isIn(player.getMainHandItem())){
            if(player.isShiftKeyDown()) {
                List<ITeleportLinkable> teleporters = MechanicalTeleporter.TELEPORT_NETWORK_HANDLER.getTeleporters(Minecraft.getInstance().level);
                for (ITeleportLinkable teleporter : teleporters) {
                    List<BlockPos> includedBlockPositions = Collections.singletonList(teleporter.getBlockPos());
                    guiGraphics.pose();

                    //double range = itemTeleport ? target.item2BlockRange() : target.block2BlockRange();
                    double range = 100;
                    double distanceSquared = teleporter.getBlockPos().distToCenterSqr(player.position());

                    if (range * range < distanceSquared || distanceSquared < TeleportHandler.MIN_TELEPORTATION_DISTANCE_SQUARED
                            || TeleportHandler.isTeleportPositionClear(mc.level, teleporter.getBlockPos()).isEmpty()) {
                        continue;
                    }
                    PoseStack poseStack = guiGraphics.pose();
                    new TeleportPointRenderer().render(teleporter, poseStack, deltaTracker.getGameTimeDeltaTicks(), true, deltaTracker.getGameTimeDeltaTicks());
                }
            }
        }


    }

}
