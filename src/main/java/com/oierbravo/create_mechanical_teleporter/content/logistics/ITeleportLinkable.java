package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public interface ITeleportLinkable {




	public boolean isAlive();
	
	public Couple<TeleportLinkNetworkHandler.Frequency> getNetworkKey();
	
	public BlockPos getLocation();

    void doTeleport(ServerPlayer pPlayer);
}
