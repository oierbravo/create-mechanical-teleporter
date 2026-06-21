package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PendingTrainSeats {
    private static final int TIMEOUT_TICKS = 100;

    private static final List<Entry> PENDING = new ArrayList<>();

    private static final class Entry {
        final UUID playerId;
        final UUID trainId;
        final int carriageId;
        final ResourceKey<Level> dimension;
        final BlockPos anchor;
        final int chunkX;
        final int chunkZ;
        int ticksRemaining = TIMEOUT_TICKS;

        Entry(UUID playerId, UUID trainId, int carriageId, ResourceKey<Level> dimension, BlockPos anchor) {
            this.playerId = playerId;
            this.trainId = trainId;
            this.carriageId = carriageId;
            this.dimension = dimension;
            this.anchor = anchor;
            this.chunkX = anchor.getX() >> 4;
            this.chunkZ = anchor.getZ() >> 4;
        }
    }

    public static void enqueue(MinecraftServer server, UUID playerId, UUID trainId, int carriageId, ResourceKey<Level> dimension, BlockPos anchor) {
        if (server == null)
            return;
        ServerLevel targetLevel = server.getLevel(dimension);
        if (targetLevel == null)
            return;

        for (Iterator<Entry> it = PENDING.iterator(); it.hasNext(); ) {
            Entry existing = it.next();
            if (existing.playerId.equals(playerId)) {
                it.remove();
                releaseChunkIfUnused(server, existing);
            }
        }

        Entry entry = new Entry(playerId, trainId, carriageId, dimension, anchor);
        targetLevel.setChunkForced(entry.chunkX, entry.chunkZ, true);
        PENDING.add(entry);
    }

    public static void tick(MinecraftServer server) {
        if (PENDING.isEmpty())
            return;
        for (Iterator<Entry> it = PENDING.iterator(); it.hasNext(); ) {
            Entry entry = it.next();

            ServerPlayer player = server.getPlayerList().getPlayer(entry.playerId);
            if (player == null) {
                it.remove();
                releaseChunkIfUnused(server, entry);
                continue;
            }

            Train train = Create.RAILWAYS.trains.get(entry.trainId);
            if (train == null || entry.carriageId < 0 || entry.carriageId >= train.carriages.size()) {
                it.remove();
                releaseChunkIfUnused(server, entry);
                continue;
            }

            Carriage carriage = train.carriages.get(entry.carriageId);
            CarriageContraptionEntity entity = carriage.anyAvailableEntity();
            if (entity != null) {
                Contraption contraption = entity.getContraption();
                int seatIndex = TeleportHandler.findFreeSeatIndex(contraption);
                if (seatIndex >= 0) {
                    ServerLevel targetLevel = server.getLevel(entry.dimension);
                    if (targetLevel != null) {
                        player.teleportTo(targetLevel, entry.anchor.getX() + 0.5, entry.anchor.getY() + 1, entry.anchor.getZ() + 0.5, player.getYRot(), player.getXRot());
                        TeleportHandler.seat(contraption, player, seatIndex);
                        player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                        it.remove();
                        releaseChunkIfUnused(server, entry);
                        continue;
                    }
                }
            }

            if (--entry.ticksRemaining <= 0) {
                it.remove();
                releaseChunkIfUnused(server, entry);
                player.displayClientMessage(ModLang.ui_no_valide_teleporter.t().component(), true);
                player.playNotifySound(SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1F, 1F);
            }
        }
    }

    private static void releaseChunkIfUnused(MinecraftServer server, Entry removed) {
        for (Entry other : PENDING) {
            if (other.dimension.equals(removed.dimension) && other.chunkX == removed.chunkX && other.chunkZ == removed.chunkZ)
                return;
        }
        ServerLevel level = server.getLevel(removed.dimension);
        if (level != null)
            level.setChunkForced(removed.chunkX, removed.chunkZ, false);
    }
}
