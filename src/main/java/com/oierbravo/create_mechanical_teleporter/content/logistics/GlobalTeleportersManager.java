package com.oierbravo.create_mechanical_teleporter.content.logistics;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalTeleportersManager {
    public Map<UUID, TeleportersNetwork> teleportersNetworks;
    private TeleportersNetworkSavedData savedData;
    public GlobalTeleportersManager() {
        teleportersNetworks = new HashMap<>();
    }

    public void levelLoaded(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null || server.overworld() != level)
            return;
        teleportersNetworks = new HashMap<>();
        savedData = null;
        loadTeleportersData(server);
    }

    public boolean mayInteract(UUID networkId, Player player) {
        TeleportersNetwork network = teleportersNetworks.get(networkId);
        return network == null || network.owner == null || !network.locked || network.owner.equals(player.getUUID());
    }

    public boolean mayAdministrate(UUID networkId, Player player) {
        TeleportersNetwork network = teleportersNetworks.get(networkId);
        return network == null || network.owner == null || network.owner.equals(player.getUUID());
    }

    public boolean isLockable(UUID networkId) {
        TeleportersNetwork network = teleportersNetworks.get(networkId);
        return network != null;
    }

    public boolean isLocked(UUID networkId) {
        TeleportersNetwork network = teleportersNetworks.get(networkId);
        return network != null && network.locked;
    }

    public void linkAdded(UUID networkId, GlobalPos pos, UUID ownedBy) {
        TeleportersNetwork network = teleportersNetworks.computeIfAbsent(networkId, $ -> new TeleportersNetwork(networkId));
        network.totalLinks.add(pos);
        if (ownedBy != null && network.owner == null)
            network.owner = ownedBy;
        markDirty();
    }

    public void linkLoaded(UUID networkId, GlobalPos pos) {
        teleportersNetworks.computeIfAbsent(networkId, $ -> new TeleportersNetwork(networkId)).loadedLinks.add(pos);
    }

    public void linkRemoved(UUID networkId, GlobalPos pos) {
        TeleportersNetwork teleportersNetwork = teleportersNetworks.get(networkId);
        if (teleportersNetwork == null)
            return;
        teleportersNetwork.totalLinks.remove(pos);
        teleportersNetwork.loadedLinks.remove(pos);
        if (teleportersNetwork.totalLinks.size() <= 0)
            teleportersNetworks.remove(networkId);
        markDirty();
    }

    public void linkInvalidated(UUID networkId, GlobalPos pos) {
        TeleportersNetwork teleportersNetwork = teleportersNetworks.get(networkId);
        if (teleportersNetwork == null)
            return;
        teleportersNetwork.loadedLinks.remove(pos);
    }

    public int getUnloadedLinkCount(UUID networkId) {
        TeleportersNetwork teleportersNetwork = teleportersNetworks.get(networkId);
        if (teleportersNetwork == null)
            return 0;
        return teleportersNetwork.totalLinks.size() - teleportersNetwork.loadedLinks.size();
    }

    private void loadTeleportersData(MinecraftServer server) {
        if (savedData != null)
            return;
        savedData = TeleportersNetworkSavedData.load(server);
        teleportersNetworks = savedData.getTeleportersNetworks();
    }

    public void tick(Level level) {
        if (level.dimension() != Level.OVERWORLD)
            return;
    }

    public void markDirty() {
        if (savedData != null)
            savedData.setDirty();
    }

}
