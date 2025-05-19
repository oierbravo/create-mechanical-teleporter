package com.oierbravo.create_mechanical_teleporter.infrastructure.network;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.ITeleportLinkable;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Set;

public class RequestTeleportHandler {
    public static final RequestTeleportHandler INSTANCE = new RequestTeleportHandler();

    public static RequestTeleportHandler get() {
        return INSTANCE;
    }

    public void handle(final RequestTeleportPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            Player player = level.getPlayerByUUID(payload.playerUUID());
            if(player instanceof ServerPlayer serverPlayer){
                Set<ITeleportLinkable> networks = MechanicalTeleporter.TELEPORT_NETWORK_HANDLER.getNetworkOf(level, payload.key());
                ITeleportLinkable destinationTeleporter = null;
                for(ITeleportLinkable teleporter : networks){
                    CompoundTag playerData = serverPlayer.getPersistentData();
                    BlockPos destin = teleporter.getLocation();
                    BlockPos playerPos = serverPlayer.getOnPos();
                    if(!teleporter.getLocation().equals(serverPlayer.getOnPos())){
                        BlockPos last = NBTHelper.readBlockPos(playerData,"lastTeleportedPos");
                        if(playerData.contains("lastTeleportedPos") &&
                                teleporter.getLocation().equals(NBTHelper.readBlockPos(playerData,"lastTeleportedPos")))
                            continue;
                        destinationTeleporter = teleporter;
                        break;
                    }
                }
                if(destinationTeleporter != null)
                    destinationTeleporter.doTeleport(serverPlayer);
            }

        });
    }
}
