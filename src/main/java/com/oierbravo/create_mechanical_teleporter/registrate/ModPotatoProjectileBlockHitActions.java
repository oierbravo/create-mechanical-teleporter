package com.oierbravo.create_mechanical_teleporter.registrate;

import com.mojang.serialization.MapCodec;
import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.infrastructure.network.RequestTeleportToBlockPosPayload;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileBlockHitAction;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;

public class ModPotatoProjectileBlockHitActions {
    static {
        register("teleport_to", ModPotatoProjectileBlockHitActions.TeleportTo.CODEC);
    }

    public static void init() {
    }

    private static void register(String name, MapCodec<? extends PotatoProjectileBlockHitAction> codec) {
        Registry.register(CreateBuiltInRegistries.POTATO_PROJECTILE_BLOCK_HIT_ACTION, ModConstants.asResource(name), codec);
    }


    public enum TeleportTo implements PotatoProjectileBlockHitAction {
        INSTANCE;
        public static final MapCodec<TeleportTo> CODEC = MapCodec.unit(INSTANCE);


        @Override
        public boolean execute(LevelAccessor levelAccessor, ItemStack projectile, BlockHitResult ray) {
            if (levelAccessor.isClientSide())
                return true;
            BlockPos hitPos = ray.getBlockPos();

            if (levelAccessor instanceof Level l && !l.isLoaded(hitPos))
                return true;

            ModMessages.sendToServer(new RequestTeleportToBlockPosPayload(hitPos));

            return true;
        }

        @Override
        public MapCodec<? extends PotatoProjectileBlockHitAction> codec() {
            return CODEC;
        }
    }
}
