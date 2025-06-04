package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterItem;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

public record TeleporterFrequency(UUID freqId, String address){
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
        return new TeleporterFrequency(HandTeleporterItem.getFrequency(itemStack), HandTeleporterItem.getAddress(itemStack));
    }
    public static TeleporterFrequency from(UUID freqId, String address){
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
}