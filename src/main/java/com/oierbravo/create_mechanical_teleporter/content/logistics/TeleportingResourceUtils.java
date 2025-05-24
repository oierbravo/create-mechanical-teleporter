package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.oierbravo.create_mechanical_teleporter.foundation.ExperienceUtils;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TeleportingResourceUtils {
    public static boolean hasEnoughAir(Player player, int amountRequired) {
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(player);
        if(backtanks.isEmpty())
            return false;
        if(!BacktankUtil.hasAirRemaining(backtanks.getFirst()))
            return false;
        return BacktankUtil.getAir(backtanks.getFirst()) >= amountRequired;
    }

    public static void consumeAir(Player player, int amount) {
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(player);
        BacktankUtil.consumeAir(player, backtanks.getFirst(), amount);
    }

    public static void consumeDurability(ItemStack itemStack, Player player, EquipmentSlot slot, int amount) {
        itemStack.hurtAndBreak(amount, player, slot);
    }


    public static boolean hasEnoughXp(Player player, int amountRequired) {
        return ExperienceUtils.getPlayerXP(player) >= amountRequired;
    }

    public static void consumeXp(Player player, int amount) {
        ExperienceUtils.addPlayerXP(player, -amount);
    }


}
