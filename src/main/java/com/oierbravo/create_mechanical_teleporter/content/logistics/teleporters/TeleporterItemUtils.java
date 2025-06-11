package com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters;

import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterBlockItem;
import com.oierbravo.create_mechanical_teleporter.registrate.ModDataComponents;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public class TeleporterItemUtils {
    public static void setFrequency(ItemStack stack, Player player, UUID frequency, String address) {
        stack.set(ModDataComponents.TELEPORTER_FREQUENCY, frequency);
        stack.set(ModDataComponents.TELEPORTER_ADDRESS, address);
        CompoundTag tag = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
        tag.putUUID("Freq", frequency);

        player.displayClientMessage(CreateLang.translateDirect("logistically_linked.tuned"), true);

        BlockEntity.addEntityType(tag, ((IBE<?>) ((BlockItem) stack.getItem()).getBlock()).getBlockEntityType());
        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
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

    public static boolean isHandTeleporterItem(ItemStack itemStack){
        if(itemStack.isEmpty())
            return false;
        return itemStack.getItem() instanceof HandTeleporterBlockItem;
    }
    public static boolean isTuned(ItemStack pStack) {
        return pStack.has(ModDataComponents.TELEPORTER_FREQUENCY);
    }

}
