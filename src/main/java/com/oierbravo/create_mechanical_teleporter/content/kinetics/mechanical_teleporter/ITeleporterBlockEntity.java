package com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter;

import com.oierbravo.create_mechanical_teleporter.content.logistics.TeleporterBehavior;

import java.util.UUID;

public interface ITeleporterBlockEntity {
    TeleporterBehavior getTeleporter();
    UUID getPlacedBy();
}
