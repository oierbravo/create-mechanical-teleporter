package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.api.registry.CreateRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class ModPotatoProjectileTypes {
    public static void bootstrap(BootstrapContext<PotatoCannonProjectileType> ctx) {
        register(ctx, "ender_potato", new PotatoCannonProjectileType.Builder()
                .damage(3)
                .reloadTicks(15)
                .velocity(1.20f)
                .knockback(0.05f)
                .renderTumbling()
                .onBlockHit(ModPotatoProjectileBlockHitActions.TeleportTo.INSTANCE)
                .addItems(ModItems.ENDER_POTATO)
                .build());

    }


    private static void register(BootstrapContext<PotatoCannonProjectileType> ctx, String name, PotatoCannonProjectileType type) {
        ctx.register(ResourceKey.create(CreateRegistries.POTATO_PROJECTILE_TYPE, ModConstants.asResource(name)), type);
    }
}
