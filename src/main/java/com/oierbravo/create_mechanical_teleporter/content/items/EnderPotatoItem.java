package com.oierbravo.create_mechanical_teleporter.content.items;

import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleportHandler;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnderPotatoItem extends Item {
    public EnderPotatoItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if(livingEntity instanceof ServerPlayer serverPlayer){
            TeleportHandler.shortRandomTeleport(level, serverPlayer, MConfigs.server().misc.enderPotatoTeleportRange.get());
        }

        return super.finishUsingItem(stack, level, livingEntity);
    }
}
