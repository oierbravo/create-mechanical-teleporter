package com.oierbravo.create_mechanical_teleporter.foundation.tileEntity.behaviour.teleport;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.logistics.ITeleportLinkable;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSpecialTextures;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.*;

public class TeleportPointDisplay {
    private static final int DISPLAY_TIME = 200;
    private static GroupEntry lastHoveredGroup = null;
    private static class Entry {
        ITeleportLinkable be;
        int timer;

        public Entry(ITeleportLinkable be) {
            this.be = be;
            timer = DISPLAY_TIME;
            Outliner.getInstance().showCluster(getOutlineKey(), createSelection(be))
                    .colored(0xFFFFFF)
                    .lineWidth(1 / 16f)
                    .withFaceTexture(AllSpecialTextures.HIGHLIGHT_CHECKERED);
        }

        protected Object getOutlineKey() {
            return Pair.of(be.getBlockPos(), 1);
        }

        protected Set<BlockPos> createSelection(ITeleportLinkable teleporter) {
            List<BlockPos> includedBlockPositions = Collections.singletonList(teleporter.getBlockPos());
            Set<BlockPos> positions = new HashSet<>(includedBlockPositions);
            return positions;
        }

    }
    private static class GroupEntry extends Entry {

        List<ITeleportLinkable> includedBEs;

        public GroupEntry(ITeleportLinkable be) {
            super(be);
        }
        /*public GroupEntry(ITeleportLinkable be) {
            super(be);
        }*/

        @Override
        protected Object getOutlineKey() {
            return this;
        }

        @Override
        protected Set<BlockPos> createSelection(ITeleportLinkable teleporterBlockEntity) {
            Set<BlockPos> list = new HashSet<>();
            includedBEs = MechanicalTeleporter.TELEPORT_NETWORK_HANDLER.getTeleporters(teleporterBlockEntity.getLevel());
            if (includedBEs == null)
                return list;
            for (ITeleportLinkable te : includedBEs)
                list.addAll(super.createSelection(te));
            return list;
        }

    }
    static Map<BlockPos, TeleportPointDisplay.Entry> entries = new HashMap<>();
    static List<TeleportPointDisplay.GroupEntry> groupEntries = new ArrayList<>();
    public static void tick() {
        Player player = Minecraft.getInstance().player;
        Level world = Minecraft.getInstance().level;
        boolean hasWand = ModItems.TELEPORT_WAND.isIn(player.getMainHandItem());
        //boolean hasWrench = AllItems.WRENCH.isIn(player.getMainHandItem());

        for (Iterator<BlockPos> iterator = entries.keySet()
                .iterator(); iterator.hasNext();) {
            BlockPos pos = iterator.next();
            TeleportPointDisplay.Entry entry = entries.get(pos);
            if (tickEntry(entry, hasWand))
                iterator.remove();
            Outliner.getInstance().keep(entry.getOutlineKey());
        }

        for (Iterator<GroupEntry> iterator = groupEntries.iterator(); iterator.hasNext();) {
            TeleportPointDisplay.GroupEntry group = iterator.next();
            if (tickEntry(group, hasWand)) {
                iterator.remove();
                if (group == lastHoveredGroup)
                    lastHoveredGroup = null;
            }
            Outliner.getInstance().keep(group.getOutlineKey());
        }

        if (!hasWand)
            return;

        HitResult over = Minecraft.getInstance().hitResult;
        if (!(over instanceof BlockHitResult))
            return;
        BlockHitResult ray = (BlockHitResult) over;
        BlockPos pos = ray.getBlockPos();
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity == null || tileEntity.isRemoved())
            return;
        if (!(tileEntity instanceof ITeleportLinkable))
            return;

        boolean ctrl = AllKeys.ctrlDown();
        ITeleportLinkable teleporterBlockEntity = (ITeleportLinkable) tileEntity;

        if (ctrl) {
            TeleportPointDisplay.GroupEntry existingGroupForPos = getExistingGroupForPos(pos);
            if (existingGroupForPos != null) {
                for (ITeleportLinkable included : existingGroupForPos.includedBEs)
                    entries.remove(included.getBlockPos());
                existingGroupForPos.timer = DISPLAY_TIME;
                return;
            }
        }

        if (!entries.containsKey(pos) || ctrl)
            display(teleporterBlockEntity);
        else {
            if (!ctrl)
                entries.get(pos).timer = DISPLAY_TIME;
        }
    }

    private static boolean tickEntry(Entry entry, boolean hasWrench) {
        ITeleportLinkable iTeleportLinkable = entry.be;
        Level teWorld = iTeleportLinkable.getLevel();
        Level world = Minecraft.getInstance().level;

        if (iTeleportLinkable.isRemoved() || teWorld == null || teWorld != world
                || !world.isLoaded(iTeleportLinkable.getBlockPos())) {
            return true;
        }

        if (!hasWrench && entry.timer > 20) {
            entry.timer = 20;
            return false;
        }

        entry.timer--;
        if (entry.timer == 0)
            return true;
        return false;
    }

    public static void display(ITeleportLinkable teleporterBlockEntity) {

        if (AllKeys.ctrlDown()) {
            GroupEntry hoveredGroup = new GroupEntry(teleporterBlockEntity);

            for (ITeleportLinkable included : hoveredGroup.includedBEs)
                Outliner.getInstance().remove(included.getBlockPos());

            groupEntries.forEach(entry -> Outliner.getInstance().remove(entry.getOutlineKey()));
            groupEntries.clear();
            entries.clear();
            groupEntries.add(hoveredGroup);
            return;
        }

        BlockPos pos = teleporterBlockEntity.getBlockPos();
        GroupEntry entry = getExistingGroupForPos(pos);
        if (entry != null)
            Outliner.getInstance().remove(entry.getOutlineKey());

        groupEntries.clear();
        entries.clear();
        entries.put(pos, new Entry(teleporterBlockEntity));

    }
    private static GroupEntry getExistingGroupForPos(BlockPos pos) {
        for (GroupEntry groupEntry : groupEntries)
            for (ITeleportLinkable teleporterBlockEntity : groupEntry.includedBEs)
                if (pos.equals(teleporterBlockEntity.getBlockPos()))
                    return groupEntry;
        return null;
    }

}