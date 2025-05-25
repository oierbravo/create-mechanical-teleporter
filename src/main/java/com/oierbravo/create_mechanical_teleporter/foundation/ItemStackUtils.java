package com.oierbravo.create_mechanical_teleporter.foundation;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemStackUtils {
    public static String getName(ItemStack itemStack){
        if(itemStack.getComponents().has(DataComponents.CUSTOM_NAME))
            return itemStack.getComponents().get(DataComponents.CUSTOM_NAME).getString();
        return Component.translatable(itemStack.getDescriptionId()).getString();
    }
}
