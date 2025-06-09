package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterFrequency;
import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportingResourceUtils;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.RequestTeleportToFrequencyWithItemPayload;
import com.oierbravo.create_mechanical_teleporter.registrate.ModDataComponents;
import com.oierbravo.create_mechanical_teleporter.registrate.ModMessages;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class HandTeleporterBlockItem extends BlockItem {

    public HandTeleporterBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static boolean isTuned(ItemStack pStack) {
        return getFrequency(pStack) != null;
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if(!isTuned(heldItem)){
            player.displayClientMessage(ModLang.ui_not_tuned.t().component(),true);
            return InteractionResultHolder.pass(heldItem);
        }


        if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND){
            if (world.isClientSide)
                CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> openScreen(player, heldItem));

            return InteractionResultHolder.success(heldItem);
        }

        if (!player.isShiftKeyDown()) {
            TeleporterFrequency teleporterFrequency = TeleporterFrequency.from(heldItem);
            if (world.isClientSide)
                if(teleporterFrequency.freqId() != null) {
                    ModMessages.sendToServer(new RequestTeleportToFrequencyWithItemPayload(teleporterFrequency,hand.equals(InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
                }

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
        if (!player.isShiftKeyDown())
            return super.useOn(pContext);

        TeleporterFrequency teleporterFrequency = TeleporterFrequency.from(level.getBlockEntity(pos));
        boolean tuned = isTuned(stack);

        if (teleporterFrequency != null) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            if (!teleporterFrequency.mayInteractMessage(player))
                return InteractionResult.SUCCESS;

            setFrequency(stack, player, teleporterFrequency.freqId(), teleporterFrequency.address());
            return InteractionResult.SUCCESS;
        }

        InteractionResult useOn = super.useOn(pContext);
        if (level.isClientSide || useOn == InteractionResult.FAIL)
            return useOn;

        player.displayClientMessage(tuned ? CreateLang.translateDirect("logistically_linked.connected")
                : CreateLang.translateDirect("logistically_linked.new_network_started"), true);
        return useOn;
    }


    public static void setFrequency(ItemStack stack, Player player, UUID frequency, String address) {
        stack.set(ModDataComponents.TELEPORTER_FREQUENCY, frequency);
        stack.set(ModDataComponents.TELEPORTER_ADDRESS, address);
        player.displayClientMessage(CreateLang.translateDirect("logistically_linked.tuned"), true);
    }

    public static void clearFrequency(ItemStack stack, Player player) {
        stack.remove(ModDataComponents.TELEPORTER_FREQUENCY);
        stack.remove(ModDataComponents.TELEPORTER_ADDRESS);
        player.displayClientMessage(ModLang.handTeleporter.message_cleared.t().component(), true);
    }
    @Nullable
    public static UUID getFrequency(ItemStack stack){
        return stack.getOrDefault(ModDataComponents.TELEPORTER_FREQUENCY, null);
    }

    public static String getAddress(ItemStack stack){
        return stack.getOrDefault(ModDataComponents.TELEPORTER_ADDRESS,"");
    }

    public static void setAddress(ItemStack stack, String address) {
        stack.set(ModDataComponents.TELEPORTER_ADDRESS, address);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext tooltipContext,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag);

        UUID freqId = stack.get(ModDataComponents.TELEPORTER_FREQUENCY);
        if(freqId == null)
            return;

        CreateLang.translate("logistically_linked.tooltip")
                .style(ChatFormatting.GOLD)
                .addTo(tooltipComponents);

        ModLang.handTeleporter.tooltip_clear.t()
                .style(ChatFormatting.GRAY)
                .addTo(tooltipComponents);

        String address = stack.get(ModDataComponents.TELEPORTER_ADDRESS);
        assert address != null;
        if(address.isEmpty())
            return;

        ModLang.handTeleporter.tooltip_address.t("hand_teleporter.tooltip.address",address)
                .style(ChatFormatting.GRAY)
                .addTo(tooltipComponents);
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreen(Player player, ItemStack stack) {
        if (Minecraft.getInstance().player == player)
            ScreenOpener.open(new HandTeleporterScreen(player.getInventory().selected,stack));
    }

    @SuppressWarnings("removal")
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new HandTeleporterItemRenderer()));
    }


    @Override
    public boolean isBarVisible(ItemStack stack) {
        if(MConfigs.server().handTeleporter.useAir.get())
            return true;
        return super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if(MConfigs.server().handTeleporter.useAir.get())
            return BacktankUtil.getBarWidth(stack, maxUses());
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if(MConfigs.server().handTeleporter.useAir.get())
            return BacktankUtil.getBarColor(stack, maxUses());
        return super.getBarColor(stack);
    }
    private static int maxUses() {
        return AllConfigs.server().equipment.airInBacktank.get() / MConfigs.server().wand.airAmount.get();
    }
    public static boolean hasEnoughResources(Player player){
        if(player.isCreative())
            return true;
        if(MConfigs.server().handTeleporter.useAir.get())
            return TeleportingResourceUtils.hasEnoughAir(player, MConfigs.server().handTeleporter.airAmount.get());
        if(MConfigs.server().handTeleporter.useDurability.get())
            return true;
        if(MConfigs.server().handTeleporter.useXp.get())
            return TeleportingResourceUtils.hasEnoughXp(player, MConfigs.server().handTeleporter.xpAmount.get());
        return true;
    }
    public static void consumeResources(ItemStack stack, Player player, EquipmentSlot slot){
        if(!isHandTeleporterItem(stack))
            return;
        if(player.isCreative())
            return;
        if(MConfigs.server().handTeleporter.useAir.get())
            TeleportingResourceUtils.consumeAir(player, MConfigs.server().handTeleporter.airAmount.get());
        if(MConfigs.server().handTeleporter.useDurability.get())
            TeleportingResourceUtils.consumeDurability(stack,player,slot,MConfigs.server().handTeleporter.durabilityAmount.get());
        if(MConfigs.server().handTeleporter.useXp.get())
            TeleportingResourceUtils.consumeXp(player,MConfigs.server().handTeleporter.xpAmount.get());
    }
    public static boolean isHandTeleporterItem(ItemStack itemStack){
        if(itemStack.isEmpty())
            return false;
        return itemStack.getItem() instanceof HandTeleporterBlockItem;
    }
}