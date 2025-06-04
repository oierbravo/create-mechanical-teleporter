package com.oierbravo.create_mechanical_teleporter.content.logistics;

import com.google.common.cache.Cache;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.foundation.utility.TickBasedCache;

import java.util.UUID;

public class TeleportersManager {
    public static final Cache<UUID, InventorySummary> ACCURATE_SUMMARIES = new TickBasedCache<>(1, false);
    public static final Cache<UUID, TeleportersSummary> SUMMARIES = new TickBasedCache<>(20, false);

    /*public static TeleportersSummary getSummaryOfNetwork(UUID freqId, boolean accurate) {
        try {
            return (accurate ? TeleportersManager.ACCURATE_SUMMARIES : TeleportersManager.SUMMARIES).get(freqId, () -> {
                TeleportersSummary summaryOfLinks = new TeleportersSummary();
                TeleporterBehavior.getAllPresent(freqId, false)
                        .forEach(link -> {
                            InventorySummary summary = link.getSummary(null);
                            if (summary != InventorySummary.EMPTY)
                                summaryOfLinks.contributingLinks++;
                            summaryOfLinks.add(summary);
                        });
                return summaryOfLinks;
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return TeleportersSummary.EMPTY;
    }*/
}
