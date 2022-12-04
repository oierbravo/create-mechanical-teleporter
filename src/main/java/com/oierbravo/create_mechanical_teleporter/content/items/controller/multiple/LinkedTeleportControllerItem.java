package com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple;

import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportLinkNetworkHandler;
import com.oierbravo.create_mechanical_teleporter.registrate.ModBlocks;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class LinkedTeleportControllerItem extends Item implements MenuProvider {
    public LinkedTeleportControllerItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return InteractionResult.PASS;
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState hitState = world.getBlockState(pos);

        if (player.mayBuild()) {
            if (player.isShiftKeyDown()) {
                if (AllBlocks.LECTERN_CONTROLLER.has(hitState)) {
                    if (!world.isClientSide)
                        AllBlocks.LECTERN_CONTROLLER.get().withTileEntityDo(world, pos, te ->
                                te.swapControllers(stack, player, ctx.getHand(), hitState));
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (ModBlocks.MECHANICAL_TELEPORTER.has(hitState)) {
                    if (world.isClientSide)
                        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.toggleBindMode(ctx.getClickedPos()));
                    player.getCooldowns()
                            .addCooldown(this, 2);
                    return InteractionResult.SUCCESS;
                }

                if (hitState.is(Blocks.LECTERN) && !hitState.getValue(LecternBlock.HAS_BOOK)) {
                    if (!world.isClientSide) {
                        ItemStack lecternStack = player.isCreative() ? stack.copy() : stack.split(1);
                        AllBlocks.LECTERN_CONTROLLER.get().replaceLectern(hitState, world, pos, lecternStack);
                    }
                    return InteractionResult.SUCCESS;
                }

                if (AllBlocks.LECTERN_CONTROLLER.has(hitState))
                    return InteractionResult.PASS;
            }
        }

        return use(world, player, ctx.getHand()).getResult();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        /*if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!world.isClientSide && player instanceof ServerPlayer && player.mayBuild())
                NetworkHooks.openScreen((ServerPlayer) player, this, buf -> {
                    buf.writeItem(heldItem);
                });
            return InteractionResultHolder.success(heldItem);
        }*/

        if (!player.isShiftKeyDown()) {
            if (world.isClientSide)
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::toggleActive);
            player.getCooldowns()
                    .addCooldown(this, 2);
        }

        return InteractionResultHolder.pass(heldItem);
    }

    @OnlyIn(Dist.CLIENT)
    private void toggleBindMode(BlockPos pos) {
        LinkedTeleportControllerClientHandler.toggleBindMode(pos);
    }

    @OnlyIn(Dist.CLIENT)
    private void toggleActive() {
        LinkedTeleportControllerClientHandler.toggle();
    }

    public static ItemStackHandler getFrequencyItems(ItemStack stack) {
        ItemStackHandler newInv = new ItemStackHandler(12);
        if (ModItems.LINKED_TELEPORT_CONTROLLER.get() != stack.getItem())
            throw new IllegalArgumentException("Cannot get frequency items from non-controller: " + stack);
        CompoundTag invNBT = stack.getOrCreateTagElement("Items");
        if (!invNBT.isEmpty())
            newInv.deserializeNBT(invNBT);
        return newInv;
    }

    public static Couple<TeleportLinkNetworkHandler.Frequency> toFrequency(ItemStack controller, int slot) {
        ItemStackHandler frequencyItems = getFrequencyItems(controller);
        return Couple.create(TeleportLinkNetworkHandler.Frequency.of(frequencyItems.getStackInSlot(slot * 2)),
                TeleportLinkNetworkHandler.Frequency.of(frequencyItems.getStackInSlot(slot * 2 + 1)));
    }

    @Override
    public Component getDisplayName() {
        return getDescription();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new LinkedTeleportControllerItemRenderer()));
    }
}
