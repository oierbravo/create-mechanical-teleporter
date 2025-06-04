package com.oierbravo.create_mechanical_teleporter.content.logistics;

import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class TeleportersNetwork {
    public UUID id;
    public Set<GlobalPos> totalLinks;
    public Set<GlobalPos> loadedLinks;
    public Set<TrainLink> trainLinks;

    public UUID owner;
    public boolean locked;

    public TeleportersNetwork(UUID networkId) {
        id = networkId;
        totalLinks = new HashSet<>();
        loadedLinks = new HashSet<>();
        trainLinks = new HashSet<>();
        owner = null;
        locked = false;
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", id);

        tag.put("Links", NBTHelper.writeCompoundList(totalLinks, p -> {
            CompoundTag nbt = new CompoundTag();
            nbt.put("Pos", NbtUtils.writeBlockPos(p.pos()));
            if (p.dimension() != Level.OVERWORLD)
                NBTHelper.writeResourceLocation(nbt, "Dim", p.dimension().location());
            return nbt;
        }));

        tag.put("TrainLinks", NBTHelper.writeCompoundList(trainLinks, p -> {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID("TrainId", p.trainId);
            nbt.putInt("CarriageIndex", p.carriageId);
            nbt.putString("Address", p.address);
            return nbt;
        }));

        if (owner != null)
            tag.putUUID("Owner", owner);

        tag.putBoolean("Locked", locked);
        return tag;
    }

    public static TeleportersNetwork read(CompoundTag tag) {
        TeleportersNetwork network = new TeleportersNetwork(tag.getUUID("Id"));

        NBTHelper.iterateCompoundList(tag.getList("Links", Tag.TAG_COMPOUND), nbt -> {
            network.totalLinks.add(GlobalPos.of(nbt.contains("Dim")
                    ? ResourceKey.create(Registries.DIMENSION, NBTHelper.readResourceLocation(nbt, "Dim"))
                    : Level.OVERWORLD, NBTHelper.readBlockPos(nbt, "Pos")));
        });

        NBTHelper.iterateCompoundList(tag.getList("TrainLinks", Tag.TAG_COMPOUND), nbt -> {
            network.trainLinks.add(new TrainLink(nbt.getUUID("TrainId"), nbt.getInt("CarriageIndex"), nbt.getString("Address")));
        });

        network.owner = tag.contains("Owner") ? tag.getUUID("Owner") : null;
        network.locked = tag.getBoolean("Locked");

        return network;
    }
    public record TrainLink(UUID trainId, int carriageId, String address){
        @Override
        public int hashCode() {
            return Objects.hash(trainId, carriageId, address);
        }

    }
}
