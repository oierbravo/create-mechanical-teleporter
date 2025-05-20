package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;
import java.util.UUID;

public record HandTeleporterItemComponent(UUID freqId, String address) {
    public static final Codec<HandTeleporterItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    UUIDUtil.CODEC.fieldOf("freqId").forGetter(HandTeleporterItemComponent::freqId),
                    Codec.STRING.fieldOf("address").forGetter(HandTeleporterItemComponent::address)
            )
            .apply(instance, HandTeleporterItemComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HandTeleporterItemComponent> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, HandTeleporterItemComponent::freqId,
                    ByteBufCodecs.STRING_UTF8, HandTeleporterItemComponent::address,
                    HandTeleporterItemComponent::new
            );

    /*@Override
    public boolean equals(Object arg0) {
        return arg0 instanceof ItemStack otherItem && ItemStack.isSameItemSameComponents(otherItem, item);
    }*/

    @Override
    public int hashCode() {
        return Objects.hash(freqId, address);
    }

}
