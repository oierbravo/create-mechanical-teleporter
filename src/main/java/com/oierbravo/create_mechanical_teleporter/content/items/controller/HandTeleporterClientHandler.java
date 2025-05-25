package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.ITeleporterBlock;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterBehavior;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;

public class HandTeleporterClientHandler {
    public static int PACKET_RATE = 5;
    private static int packetCooldown;


    public static void tick() {
        /*HandTeleporterItemRenderer.tick();

        if (packetCooldown > 0)
            packetCooldown--;*/

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;
        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof HandTeleporterItem)
                || !HandTeleporterItem.isTuned(mainHandItem))
            return;

        CompoundTag tag = mainHandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.hasUUID("Freq"))
            return;

        UUID uuid = tag.getUUID("Freq");

        for (TeleporterBehavior behaviour : TeleporterBehavior.getAllPresent(uuid, false, true)) {
            SmartBlockEntity be = behaviour.blockEntity;
            //outlineBlock(be.getBlockState(),be.getBlockPos(),player);
            VoxelShape shape = be.getBlockState()
                    .getShape(player.level(), be.getBlockPos());
            if (shape.isEmpty())
                continue;
            if (!player.blockPosition()
                    .closerThan(be.getBlockPos(), 64))
                continue;
            for (int i = 0; i < shape.toAabbs()
                    .size(); i++) {
                AABB aabb = shape.toAabbs()
                        .get(i);
                Outliner.getInstance()
                        .showAABB(Pair.of(behaviour, i), aabb.inflate(-1 / 128f)
                                .move(be.getBlockPos()), 2)
                        .lineWidth(1 / 32f)
                        .disableLineNormals()
                        .colored(AnimationTickHolder.getTicks() % 16 < 8 ? 0x009999 : 0x008888);
            }

        }
        /*Set<TeleportersNetwork.TrainLink> trainLinks = MechanicalTeleporter.TELEPORTERS.teleportersNetworks.get(uuid).trainLinks;
        for(TeleportersNetwork.TrainLink trainLink : trainLinks){
            Train train = Create.RAILWAYS.trains.get(trainLink.trainId());
            if(train == null)
                continue;
            Carriage carriage = train.carriages.get(trainLink.carriageId());
            Contraption contraption = carriage.anyAvailableEntity().getContraption();
            renderContraptionOutline(contraption, player);
        }*/
    }
    private static void renderContraptionOutline(Contraption contraption, Player player) {
        for(MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor :contraption.getActors()){
            if(actor.getLeft().state().getBlock() instanceof ITeleporterBlock iTeleporterBlock){
                //outlineBlock(actor.getLeft().state(), actor.getRight().position.normalize(), player);
            }
        }
        /*if(contraption.simplifiedEntityColliders.isPresent()){
            List<AABB> colliders = contraption.simplifiedEntityColliders.get();
            for (int i = 0; i < colliders
                    .size(); i++) {
                AABB aabb = colliders
                        .get(i);
                Outliner.getInstance()
                        .showAABB(Pair.of(colliders, i), aabb.inflate(-1 / 128f)
                                .move(contraption.entity.position())., 2)

                        .lineWidth(1 / 32f)
                        .disableCull()
                        .disableLineNormals()
                        .colored(AnimationTickHolder.getTicks() % 16 < 8 ? 0x009999 : 0x008888);
            }
        }*/
    }
    private static void outlineBlock(BlockState blockState, Vec3 position, Player player){
        VoxelShape shape = blockState
                .getShape(player.level(), BlockPos.containing(position));
        if (shape.isEmpty())
            return;
        if (!player.blockPosition()
                .closerThan(BlockPos.containing(position), 64))
            return;
        for (int i = 0; i < shape.toAabbs()
                .size(); i++) {
            AABB aabb = shape.toAabbs()
                    .get(i);
            Outliner.getInstance()
                    .showAABB(Pair.of(blockState, i), aabb.inflate(-1 / 128f)
                            .move(position), 2)
                    .lineWidth(1 / 32f)
                    .disableLineNormals()
                    .colored(AnimationTickHolder.getTicks() % 16 < 8 ? 0x009999 : 0x008888);
        }
    }
    private static void outlineBlock(BlockState blockState, BlockPos blockPos, Player player){
        outlineBlock(blockState, blockPos.getCenter(), player);
    }
}
