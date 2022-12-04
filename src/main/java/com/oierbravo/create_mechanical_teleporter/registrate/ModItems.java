package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple.LinkedTeleportControllerItem;
import com.oierbravo.create_mechanical_teleporter.content.items.controller.simple.SimpleTeleportControllerItem;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class ModItems {


    private static final CreateRegistrate REGISTRATE = MechanicalTeleporter.registrate()
            .creativeModeTab(() ->  Create.BASE_CREATIVE_TAB);

    static {
        REGISTRATE.startSection(AllSections.LOGISTICS);
    }
   /* public static final ItemEntry<LinkedTeleportControllerItem> LINKED_TELEPORT_CONTROLLER =
            REGISTRATE.item("linked_teleport_controller", LinkedTeleportControllerItem::new)
                    .properties(p -> p.stacksTo(1))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();
*/
    public static final ItemEntry<SimpleTeleportControllerItem> SIMPLE_TELEPORT_CONTROLLER =
            REGISTRATE.item("simple_teleport_controller", SimpleTeleportControllerItem::new)
                    .properties(p -> p.stacksTo(1))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();

    public static void register() {

        //Create.registrate().addToSection(LINKED_TELEPORT_CONTROLLER, AllSections.LOGISTICS);
        //Create.registrate().addToSection(SIMPLE_TELEPORT_CONTROLLER, AllSections.LOGISTICS);
        Create.REGISTRATE.addToSection(SIMPLE_TELEPORT_CONTROLLER, AllSections.LOGISTICS);
    }

}
