package com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters;

import com.oierbravo.create_mechanical_teleporter.content.logistics.IHaveTeleportFrequency;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class AbstractTeleporterBlockItem extends BlockItem implements IHaveTeleportFrequency {

    public AbstractTeleporterBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();

        if (player == null)
            return InteractionResult.FAIL;
        if (!player.isShiftKeyDown())
            return super.useOn(pContext);

        TeleporterFrequency teleporterFrequency = TeleporterFrequency.from(level.getBlockEntity(pos));
        boolean tuned = TeleporterItemUtils.isTuned(stack);

        if (teleporterFrequency != null) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            if (!teleporterFrequency.mayInteractMessage(player))
                return InteractionResult.SUCCESS;

            TeleporterItemUtils.setFrequency(stack, player, teleporterFrequency.freqId(), teleporterFrequency.address());
            return InteractionResult.SUCCESS;
        }

        InteractionResult useOn = super.useOn(pContext);
        if (level.isClientSide || useOn == InteractionResult.FAIL)
            return useOn;

        player.displayClientMessage(tuned ? CreateLang.translateDirect("logistically_linked.connected")
                : CreateLang.translateDirect("logistically_linked.new_network_started"), true);
        return useOn;
    }

    @Override
    public TeleporterFrequency getFrequency() {
        return null;
    }
}
