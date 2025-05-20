package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportersNetworkSavedData extends SavedData{
    private Map<UUID, TeleportersNetwork> teleportersNetworks = new HashMap<>();

    public static SavedData.Factory<TeleportersNetworkSavedData> factory() {
        return new SavedData.Factory<>(TeleportersNetworkSavedData::new, TeleportersNetworkSavedData::load);
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        GlobalTeleportersManager teleporters = MechanicalTeleporter.TELEPORTERS;
        nbt.put("TeleportersNetworks",
                NBTHelper.writeCompoundList(teleporters.teleportersNetworks.values(), TeleportersNetwork::write));
        return nbt;
    }

    private static TeleportersNetworkSavedData load(CompoundTag nbt, HolderLookup.Provider registries) {
        TeleportersNetworkSavedData sd = new TeleportersNetworkSavedData();
        sd.teleportersNetworks = new HashMap<>();
        NBTHelper.iterateCompoundList(nbt.getList("TeleportersNetworks", Tag.TAG_COMPOUND), c -> {
            TeleportersNetwork network = TeleportersNetwork.read(c);
            sd.teleportersNetworks.put(network.id, network);
        });
        return sd;
    }

    public Map<UUID, TeleportersNetwork> getTeleportersNetworks() {
        return teleportersNetworks;
    }

    private TeleportersNetworkSavedData() {}

    public static TeleportersNetworkSavedData load(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(factory(), "mechanicals_teleporters");
    }
}
