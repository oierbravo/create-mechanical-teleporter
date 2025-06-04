package com.oierbravo.create_mechanical_teleporter.content.logistics;

import net.minecraft.core.GlobalPos;

import java.util.UUID;

public class TeleportersSummary {
    public static final TeleportersSummary EMPTY = new TeleportersSummary();

    public static enum TeleporterType {
        GLOBALPOS, TRAIN
    }
    public static class TeleporterEntry{
        public TeleporterType type;
        public String address;

        public TeleporterEntry(TeleporterType type, String address){
            this.type = type;
            this.address = address;
        }
    }
    public static class TeleporterEntryGlobalPos extends TeleporterEntry{
        public GlobalPos globalPos;
        public TeleporterEntryGlobalPos(String address, GlobalPos globalPos){
            super(TeleporterType.GLOBALPOS, address);
            this.globalPos = globalPos;

        }
    }
    public static class TeleporterEntryTrain extends TeleporterEntry{
        public UUID trainId;
        public TeleporterEntryTrain(String address, UUID trainId){
            super(TeleporterType.GLOBALPOS, address);
            this.trainId = trainId;

        }
    }
}
