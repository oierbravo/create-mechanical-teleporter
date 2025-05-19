package com.oierbravo.create_mechanical_teleporter.content.logistics;

import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.UUID;

public interface ITeleportLinkable {

	public boolean isAlive();
	
	public Couple<TeleportLinkNetworkHandler.Frequency> getNetworkKey();
	
	public BlockPos getLocation();

    void doTeleport(ServerPlayer pPlayer);

	UUID getUUID();

	boolean isPrivate();
	BlockPos getBlockPos();
	Level getLevel();
	boolean isRemoved();

	String name();
}
