package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface ITeleportLinkable {

	public boolean isAlive();
	
	public UUID getNetworkKey();
	
	public BlockPos getLocation();

    void doTeleport(ServerPlayer pPlayer);

	UUID getUUID();
}
