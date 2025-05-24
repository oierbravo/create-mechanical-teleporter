package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterItem;
import com.oierbravo.create_mechanical_teleporter.content.items.wand.TeleportWandItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

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
                    .properties(p -> p.stacksTo(1).durability(200))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();

    public static final ItemEntry<Item> ENDER_QUARTZ =
            REGISTRATE.item("ender_quartz", Item::new)
                    .lang("Ender quartz")
                    .register();

    public static final ItemEntry<Item> POLISHED_ENDER_QUARTZ =
            REGISTRATE.item("polished_ender_quartz", Item::new)
                    .lang("Polished ender quartz")
                    .register();

    public static void register() {}

}
