package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterBlockItem;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;
import java.util.UUID;

public record TeleporterFrequency(UUID freqId, String address){
    public static final TeleporterFrequency EMPTY = new TeleporterFrequency(UUID.randomUUID(),"");
    public static final Codec<TeleporterFrequency> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    UUIDUtil.CODEC.fieldOf("freqId").forGetter(TeleporterFrequency::freqId),
                    Codec.STRING.fieldOf("address").forGetter(TeleporterFrequency::address)
            )
            .apply(instance, TeleporterFrequency::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleporterFrequency> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, TeleporterFrequency::freqId,
            ByteBufCodecs.STRING_UTF8, TeleporterFrequency::address,
            TeleporterFrequency::new
    );

    public static TeleporterFrequency from(BlockEntity blockEntity){
        if(blockEntity instanceof IHaveTeleportFrequency teleporterBlockEntity)
            return teleporterBlockEntity.getFrequency();
        return null;
    }
    public static TeleporterFrequency from(TeleporterBehavior teleporterBehavior){
        return new TeleporterFrequency(teleporterBehavior.freqId, teleporterBehavior.signBasedAddress);
    }
    public static TeleporterFrequency from(ItemStack itemStack){
        return new TeleporterFrequency(HandTeleporterBlockItem.getFrequency(itemStack), HandTeleporterBlockItem.getAddress(itemStack));
    }
    public static TeleporterFrequency from(UUID freqId, String address){
        if(freqId == null)
            return null;
        return new TeleporterFrequency(freqId, address);
    }

    public boolean isPresent() {
        return freqId != null;
    }
    public boolean isEmpty(){
        return freqId == null;
    }

    public boolean mayInteractMessage(Player player) {
        boolean mayInteract = MechanicalTeleporter.TELEPORTERS.mayInteract(freqId, player);
        if (!mayInteract)
            player.displayClientMessage(CreateLang.translate("logistically_linked.protected")
                    .style(ChatFormatting.RED)
                    .component(), true);
        return mayInteract;
    }
    public boolean mayInteract(Player player) {
        return MechanicalTeleporter.TELEPORTERS.mayInteract(freqId, player);
    }


    public boolean mayAdministrate(Player player) {
        return MechanicalTeleporter.TELEPORTERS.mayAdministrate(freqId, player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(freqId, address);
    }
}