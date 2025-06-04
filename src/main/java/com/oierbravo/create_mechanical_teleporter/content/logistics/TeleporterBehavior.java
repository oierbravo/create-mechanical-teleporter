package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.google.common.cache.Cache;
import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.ITeleporterBlockEntity;
import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.TickBasedCache;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class TeleporterBehavior extends BlockEntityBehaviour {

    public static final BehaviourType<TeleporterBehavior> TYPE = new BehaviourType<>();
    private TeleporterBehaviourSpecifics specifics;

    public static final AtomicInteger LINK_ID_GENERATOR = new AtomicInteger();
    public int linkId;

    public int redstonePower;
    public UUID freqId;
    public String signBasedAddress;

    private boolean addedGlobally = false;
    private boolean loadedGlobally = false;
    private boolean global = false;

    private GlobalPos globalPos;

    private boolean isTeleportable;

    private final TELEPORTER_TYPES teleporterType;

    private static final Cache<UUID, Cache<Integer, WeakReference<TeleporterBehavior>>> LINKS =
            new TickBasedCache<>(20, true);

    private static final Cache<UUID, Cache<Integer, WeakReference<TeleporterBehavior>>> CLIENT_LINKS =
            new TickBasedCache<>(20, true, true);

    public <T extends SmartBlockEntity & TeleporterBehaviourSpecifics> TeleporterBehavior(T be, boolean global) {
        super(be);
        this.specifics = be;
        this.global = global;
        linkId = LINK_ID_GENERATOR.getAndIncrement();
        freqId = UUID.randomUUID();
        signBasedAddress = "";
        teleporterType = specifics.getTeleporterType();
        isTeleportable = specifics.getTeleporterType() != TELEPORTER_TYPES.MANAGER;
        globalPos = GlobalPos.of(getDimension(),getPos());
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
                .filter(TeleporterBehavior::isValidLoadedLink);


        if (sortByPriority)
            stream = stream.sorted((e1, e2) -> Integer.compare(e1.redstonePower, e2.redstonePower));

        return stream.toList();
    }

    public static Collection<TeleporterBehavior> getAll(UUID freq, boolean sortByPriority,
                                                               boolean clientSide) {
        Cache<Integer, WeakReference<TeleporterBehavior>> cache =
                (clientSide ? CLIENT_LINKS : LINKS).getIfPresent(freq);
        if (cache == null)
            return Collections.emptyList();
        Stream<TeleporterBehavior> stream = new LinkedList<>(cache.asMap()
                .values()).stream()
                .map(WeakReference::get)
                .filter(TeleporterBehavior::isValidLoadedLink);


        //if (sortByPriority)
        //    stream = stream.sorted((e1, e2) -> Integer.compare(e1.redstonePower, e2.redstonePower));

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
        if (blockEntity.getLevel().isClientSide())
            return;
        updateSignAddress();
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
            if (blockEntity instanceof ITeleporterBlockEntity tbe)
                MechanicalTeleporter.TELEPORTERS.linkAdded(freqId, getGlobalPos(), tbe.getPlacedBy());
        }

    }

    public GlobalPos getGlobalPos() {
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

    public static boolean isValidLoadedLink(TeleporterBehavior link) {
        return link != null && !link.blockEntity.isRemoved() && !link.blockEntity.isChunkUnloaded();
    }

    public static boolean isValidLink(TeleporterBehavior link) {
        return link != null;
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
        tag.putBoolean("Teleportable", isTeleportable);
        tag.putString("SignAddress", signBasedAddress);
        if (globalPos.dimension() != Level.OVERWORLD)
            NBTHelper.writeResourceLocation(tag, "Dim", globalPos.dimension().location());
        tag.put("Pos", NbtUtils.writeBlockPos(globalPos.pos()));

    }

    @Override
    public void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.hasUUID("Freq"))
            freqId = tag.getUUID("Freq");
        redstonePower = tag.getInt("Power");
        addedGlobally = tag.getBoolean("Added");
        isTeleportable = tag.getBoolean("Teleportable");
        signBasedAddress = tag.getString("SignAddress");
        globalPos = GlobalPos.of(tag.contains("Dim")
                ? ResourceKey.create(Registries.DIMENSION, NBTHelper.readResourceLocation(tag, "Dim"))
                : Level.OVERWORLD, NBTHelper.readBlockPos(tag, "Pos"));


    }
    protected void updateSignAddress() {
        if(!isTeleportable)
            return;
        signBasedAddress = "";
        for (Direction side : Iterate.directions) {
            String address = getSign(side);
            if (address == null || address.isBlank())
                continue;
            signBasedAddress = address;
        }
    }
    protected boolean checkForTeleporter(){
        for (Direction side : Iterate.directions) {
            String address = getSign(side);
            if (address == null || address.isBlank())
                continue;
            signBasedAddress = address;
        }
        return false;
    }
    protected String getSign(Direction side) {
        BlockEntity sideBlockEntity = blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos().relative(side));
        if (!(sideBlockEntity instanceof SignBlockEntity sign))
            return null;
        for (boolean front : Iterate.trueAndFalse) {
            SignText text = sign.getText(front);
            String address = "";
            for (Component component : text.getMessages(false)) {
                String string = component.getString();
                if (!string.isBlank())
                    address += string.trim() + " ";
            }
            if (!address.isBlank())
                return address.trim();
        }
        return null;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public boolean checkRequerimentsForTeleport() {
        if(!isTeleportable)
            return false;
        return specifics.checkRequerimentsForTeleport();
    }

    public void consumeResources() {
        specifics.consumeResources();
    }

    public List<Component> getTooltips(){
        ArrayList<Component> lines = new ArrayList<>();
        //lines.add(ModLang.teleporterManager.guiDimension.t(teleporterBehavior.getGlobalPos().dimension().location().toString()).component());
        lines.add(ModLang.teleporterManager.guiPos.t(this.globalPos.pos().toShortString()).component());
        return lines;
    }
    private ResourceKey<Level> getDimension(){
        if(blockEntity.getLevel() == null)
            return Level.OVERWORLD;
        return blockEntity.getLevel().dimension();
    }

    public boolean isTeleportable() {
        return  isTeleportable;
    }
    public TELEPORTER_TYPES getTeleporterType(){
        return teleporterType;
    }

    public interface TeleporterBehaviourSpecifics {
        default boolean checkRequerimentsForTeleport(){
            return true;
        };
        default void consumeResources(){};
        TELEPORTER_TYPES getTeleporterType();
    }
    public enum TELEPORTER_TYPES {
        MECHANICAL,
        CREATIVE,
        MANAGER,
        TRAIN
    }
}
