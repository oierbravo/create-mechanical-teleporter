package com.oierbravo.create_mechanical_teleporter.content.machines.mechanical_teleporter;

import com.google.common.cache.Cache;
import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.TickBasedCache;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class TeleporterBehavior extends BlockEntityBehaviour {

    public static final BehaviourType<TeleporterBehavior> TYPE = new BehaviourType<>();

    public static final AtomicInteger LINK_ID_GENERATOR = new AtomicInteger();
    public int linkId;

    public int redstonePower;
    public UUID freqId;

    private boolean addedGlobally = false;
    private boolean loadedGlobally = false;
    private boolean global = false;

    private static final Cache<UUID, Cache<Integer, WeakReference<TeleporterBehavior>>> LINKS =
            new TickBasedCache<>(20, true);

    private static final Cache<UUID, Cache<Integer, WeakReference<TeleporterBehavior>>> CLIENT_LINKS =
            new TickBasedCache<>(20, true, true);

    public TeleporterBehavior(SmartBlockEntity be, boolean global) {
        super(be);
        this.global = global;
        linkId = LINK_ID_GENERATOR.getAndIncrement();
        freqId = UUID.randomUUID();
    }

    public static Collection<TeleporterBehavior> getAllPresent(UUID freq, boolean sortByPriority) {
        return getAllPresent(freq, sortByPriority, false);
    }

    public static Collection<TeleporterBehavior> getAllPresent(UUID freq, boolean sortByPriority,
                                                                        boolean clientSide) {
        Cache<Integer, WeakReference<TeleporterBehavior>> cache =
                (clientSide ? CLIENT_LINKS : LINKS).getIfPresent(freq);
        if (cache == null)
            return Collections.emptyList();
        Stream<TeleporterBehavior> stream = new LinkedList<>(cache.asMap()
                .values()).stream()
                .map(WeakReference::get)
                .filter(TeleporterBehavior::isValidLink);

        if (sortByPriority)
            stream = stream.sorted((e1, e2) -> Integer.compare(e1.redstonePower, e2.redstonePower));

        return stream.toList();
    }

    public static void keepAlive(TeleporterBehavior behaviour) {
        boolean onClient = behaviour.blockEntity.getLevel().isClientSide;
        if (behaviour.redstonePower == 15)
            return;
        try {
            Cache<Integer, WeakReference<TeleporterBehavior>> cache =
                    (onClient ? CLIENT_LINKS : LINKS).get(behaviour.freqId, () -> new TickBasedCache<>(400, false));

            if (cache == null)
                return;

            WeakReference<TeleporterBehavior> reference =
                    cache.get(behaviour.linkId, () -> new WeakReference<>(behaviour));
            cache.put(behaviour.linkId, reference.get() != behaviour ? new WeakReference<>(behaviour) : reference);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void remove(TeleporterBehavior behaviour) {
        Cache<Integer, WeakReference<TeleporterBehavior>> cache = LINKS.getIfPresent(behaviour.freqId);
        if (cache != null)
            cache.invalidate(behaviour.linkId);
    }

    //

    @Override
    public void unload() {
        if (loadedGlobally && global && getWorld() != null)
            MechanicalTeleporter.TELEPORTERS.linkInvalidated(freqId, getGlobalPos());
        super.unload();
        remove(this);
    }

    @Override
    public void lazyTick() {
        keepAlive(this);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (getWorld().isClientSide)
            return;

        if (!loadedGlobally && global) {
            loadedGlobally = true;
            MechanicalTeleporter.TELEPORTERS.linkLoaded(freqId, getGlobalPos());
        }

        if (!addedGlobally && global) {
            addedGlobally = true;
            blockEntity.setChanged();
            if (blockEntity instanceof TeleporterBlockEntity tbe)
                MechanicalTeleporter.TELEPORTERS.linkAdded(freqId, getGlobalPos(), tbe.placedBy);
        }

    }

    private GlobalPos getGlobalPos() {
        return GlobalPos.of(getWorld().dimension(), getPos());
    }

    @Override
    public void destroy() {
        super.destroy();
        if (addedGlobally && global && getWorld() != null)
            MechanicalTeleporter.TELEPORTERS.linkRemoved(freqId, getGlobalPos());
    }

    public void redstonePowerChanged(int power) {
        if (power == redstonePower)
            return;
        redstonePower = power;
        blockEntity.setChanged();

        if (power == 15)
            remove(this);
        else
            keepAlive(this);
    }



    //

    public boolean mayInteract(Player player) {
        return MechanicalTeleporter.TELEPORTERS.mayInteract(freqId, player);
    }

    public boolean mayInteractMessage(Player player) {
        boolean mayInteract = MechanicalTeleporter.TELEPORTERS.mayInteract(freqId, player);
        if (!mayInteract)
            player.displayClientMessage(CreateLang.translate("logistically_linked.protected")
                    .style(ChatFormatting.RED)
                    .component(), true);
        return mayInteract;
    }

    public boolean mayAdministrate(Player player) {
        return MechanicalTeleporter.TELEPORTERS.mayAdministrate(freqId, player);
    }

    public static boolean isValidLink(TeleporterBehavior link) {
        return link != null && !link.blockEntity.isRemoved() && !link.blockEntity.isChunkUnloaded();
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putUUID("Freq", freqId);
    }

    @Override
    public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putUUID("Freq", freqId);
        tag.putInt("Power", redstonePower);
        tag.putBoolean("Added", addedGlobally);
    }

    @Override
    public void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.hasUUID("Freq"))
            freqId = tag.getUUID("Freq");
        redstonePower = tag.getInt("Power");
        addedGlobally = tag.getBoolean("Added");
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
