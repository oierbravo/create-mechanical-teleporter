package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterItem;
import com.oierbravo.create_mechanical_teleporter.content.items.wand.TeleportWandItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

public class ModItems {


    private static final CreateRegistrate REGISTRATE = MechanicalTeleporter.registrate();

    public static final ItemEntry<TeleportWandItem> TELEPORT_WAND =
            REGISTRATE.item("teleport_wand", TeleportWandItem::new)
                    .lang("Teleport Wand")
                    .properties(p -> p.stacksTo(1))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();

    public static final ItemEntry<HandTeleporterItem> HAND_TELEPORTER =
            REGISTRATE.item("hand_teleporter", HandTeleporterItem::new)
                    .lang("Hand teleporter")
                    .properties(p -> p.stacksTo(1))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();

    public static void register() {}

}
