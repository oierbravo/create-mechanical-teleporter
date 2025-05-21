package com.oierbravo.create_mechanical_teleporter.foundation;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.MConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.world.chunk.TicketController;

public class ChunkManager {
    public static final TicketController TICKET_CONTROLLER = new TicketController(ModConstants.asResource("chunk_loader"), null);
    public static void init(){};

    public static void loadForcedChunks(Level level, BlockPos blockPos){
        if(level == null)
            return;

        if(level instanceof ServerLevel serverLevel) {
            int range = MConfigs.server().teleporter.autoChunkLoadRange.get();
            ChunkPos chunkPos = new ChunkPos(blockPos);
            for (int i = -range; i <= range; i++) {
                for (int j = -range; j <= range; j++) {
                    ChunkManager.TICKET_CONTROLLER.forceChunk(serverLevel, blockPos, chunkPos.x, chunkPos.z, true, true);
                }
            }
        }
    }

    public static void unLoadForcedChunks(Level level, BlockPos blockPos){
        if(level == null)
            return;
        if(level instanceof ServerLevel serverLevel){
            int range = MConfigs.server().teleporter.autoChunkLoadRange.get();
            ChunkPos chunkPos = new ChunkPos(blockPos);
            for (int i = -range; i <= range; i++) {
                for (int j = -range; j <= range; j++) {
                    ChunkManager.TICKET_CONTROLLER.forceChunk(serverLevel, blockPos, chunkPos.x, chunkPos.z, false, true);
                }
            }
        }
    }
}
