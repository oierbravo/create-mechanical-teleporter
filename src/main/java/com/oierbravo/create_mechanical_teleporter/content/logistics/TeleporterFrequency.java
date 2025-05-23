package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterItem;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterBehavior;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterBlockEntity;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record TeleporterFrequency(UUID freqId, String address){
        public static final StreamCodec<RegistryFriendlyByteBuf, TeleporterFrequency> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, TeleporterFrequency::freqId,
                ByteBufCodecs.STRING_UTF8, TeleporterFrequency::address,
                TeleporterFrequency::new
        );

        public static TeleporterFrequency from(TeleporterBlockEntity teleporterBlockEntity){
            return from(teleporterBlockEntity.teleporterBehavior);
        }
        public static TeleporterFrequency from(TeleporterBehavior teleporterBehavior){
            return new TeleporterFrequency(teleporterBehavior.freqId, teleporterBehavior.signBasedAddress);
        }
        public static TeleporterFrequency from(ItemStack itemStack){
            return new TeleporterFrequency(HandTeleporterItem.getFrequency(itemStack), HandTeleporterItem.getAddress(itemStack));
        }

    public boolean isPresent() {
        return freqId != null;
    }
    public boolean isEmpty(){
        return freqId == null;
    }
}