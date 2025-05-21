package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter.TeleporterBehavior;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.RequestTeleportToFrequencyPayload;
import com.oierbravo.create_mechanical_teleporter.registrate.ModMessages;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class HandTeleporterItem extends Item {
    public HandTeleporterItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    private void clickActivate() {
        //SimpleTeleportControllerClientHandler.activate();
        //ModMessages.sendToServer(new RequestTeleportToFrequencyPayload(getFrequency()));
    }
    public static boolean isTuned(ItemStack pStack) {
        return getFrequency(pStack) != null;
    }
    @Override
    public boolean isFoil(@NotNull ItemStack pStack) {
        return isTuned(pStack);
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        /*if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!world.isClientSide && player instanceof ServerPlayer && player.mayBuild())
                player.openMenu(this, buf -> {
                    ItemStack.STREAM_CODEC.encode(buf, heldItem);
                });
            return InteractionResultHolder.success(heldItem);
        }*/
        if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND){
            clearFrequency(heldItem, player);
        }

        if (!player.isShiftKeyDown()) {
            if (world.isClientSide)
                //CatnipServices.PLATFORM.executeOnClientOnly(() -> {
                if(getFrequency(heldItem) != null) {
                    ModMessages.sendToServer(new RequestTeleportToFrequencyPayload(getFrequency(heldItem)));
                }

                //    return true;
                //});
            //CatnipServices.PLATFORM.executeOnClientOnly(() -> this::toggleActive);
            player.getCooldowns()
                    .addCooldown(this, 2);
        }

        return InteractionResultHolder.pass(heldItem);
    }
    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();

        if (player == null)
            return InteractionResult.FAIL;
        if (player.isShiftKeyDown())
            return super.useOn(pContext);

        TeleporterBehavior link = BlockEntityBehaviour.get(level, pos, TeleporterBehavior.TYPE);
        boolean tuned = isTuned(stack);

        if (link != null) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            if (!link.mayInteractMessage(player))
                return InteractionResult.SUCCESS;

            assignFrequency(stack, player, link.freqId);
            return InteractionResult.SUCCESS;
        }

        InteractionResult useOn = super.useOn(pContext);
        if (level.isClientSide || useOn == InteractionResult.FAIL)
            return useOn;

        player.displayClientMessage(tuned ? CreateLang.translateDirect("logistically_linked.connected")
                : CreateLang.translateDirect("logistically_linked.new_network_started"), true);
        return useOn;
    }


    public static void assignFrequency(ItemStack stack, Player player, UUID frequency) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putUUID("Freq", frequency);

        player.displayClientMessage(CreateLang.translateDirect("logistically_linked.tuned"), true);

        //BlockEntity.addEntityType(tag, ((IBE<?>) ((BlockItem) stack.getItem()).getBlock()).getBlockEntityType());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void clearFrequency(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.remove("Freq");

        player.displayClientMessage(CreateLang.translateDirect("logistically_linked.tuned"), true);

        //BlockEntity.addEntityType(tag, ((IBE<?>) ((BlockItem) stack.getItem()).getBlock()).getBlockEntityType());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    @Nullable
    public static UUID getFrequency(ItemStack stack){
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if(!tag.contains("Freq"))
            return null;
        return tag.getUUID("Freq");
    }


}