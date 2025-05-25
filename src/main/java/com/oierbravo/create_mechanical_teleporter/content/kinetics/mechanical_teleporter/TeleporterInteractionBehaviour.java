package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter;

import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterItem;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class TeleporterInteractionBehaviour extends MovingInteractionBehaviour {
    @Override
    protected void setContraptionActorData(AbstractContraptionEntity contraptionEntity, int index, StructureTemplate.StructureBlockInfo info, MovementContext ctx) {
        super.setContraptionActorData(contraptionEntity, index, info, ctx);
    }

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        ItemStack stack = player.getItemInHand(activeHand);
        if(ModItems.HAND_TELEPORTER.isIn(stack)){
            Contraption contraption = contraptionEntity.getContraption();
            BlockEntity blockEntity = contraption.presentBlockEntities.get(localPos);
            if(blockEntity instanceof ITeleporterBlockEntity iTeleporterBlockEntity){
                HandTeleporterItem.assignFrequency(stack, player, iTeleporterBlockEntity.getTeleporter().freqId, iTeleporterBlockEntity.getTeleporter().signBasedAddress);
            }
        }
        return super.handlePlayerInteraction(player, activeHand, localPos, contraptionEntity);
    }

    @Override
    protected void setContraptionBlockData(AbstractContraptionEntity contraptionEntity, BlockPos pos, StructureTemplate.StructureBlockInfo info) {
        super.setContraptionBlockData(contraptionEntity, pos, info);
    }


}
