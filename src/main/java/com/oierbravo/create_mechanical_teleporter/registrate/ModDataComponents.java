package com.oierbravo.create_mechanical_teleporter.registrate;

import com.mojang.serialization.Codec;
import com.oierbravo.create_mechanical_teleporter.ModConstants;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class ModDataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ModConstants.MODID);

   /* public static final DataComponentType<TeleporterFrequency> TELEPORTER_FREQUENCY = register(
            "hand_teleporter",
            builder -> builder.persistent(TeleporterFrequency.CODEC).networkSynchronized(TeleporterFrequency.STREAM_CODEC)
    );*/

    public static final DataComponentType<UUID> TELEPORTER_FREQUENCY = register(
            "teleporter_frquency",
            builder -> builder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC)
    );
    public static final DataComponentType<String> TELEPORTER_ADDRESS = register(
            "teleporter_address",
            builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8)
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
