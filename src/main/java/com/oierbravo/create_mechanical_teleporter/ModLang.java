package com.oierbravo.create_mechanical_teleporter;

import com.oierbravo.create_mechanical_teleporter.content.items.controller.HandTeleporterLang;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.MechanicalTeleporterLang;
import com.oierbravo.create_mechanical_teleporter.content.logistics.manager.TeleporterManagerLang;
import com.oierbravo.mechanicals.utility.lang.MechanicalLangBuilder;
import com.oierbravo.mechanicals.utility.lang.MechanicalRegistrateLangBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;

import static com.oierbravo.create_mechanical_teleporter.ModConstants.DISPLAY_NAME;

public class ModLang {
    private static <LB extends MechanicalRegistrateLangBuilder<?>> LB langBuilder(){
        return (LB) new ModLangBuilder();
    }

    public static MechanicalLangBuilder creativeTab = langBuilder().addCreativeTab(DISPLAY_NAME);
    public static MechanicalLangBuilder chunkLoader_loaded = langBuilder().add("chunk_loader.loaded", "Chunk loaded");
    public static MechanicalLangBuilder ui_no_valide_teleporter = langBuilder().add("ui.no_valid_teleporter", "No valid teleporter found");
    public static MechanicalLangBuilder ui_not_tuned = langBuilder().add("ui.not_tuned", "Not tuned to a network");
    public static TeleporterManagerLang teleporterManager = new TeleporterManagerLang();
    public static HandTeleporterLang handTeleporter = new HandTeleporterLang();
    public static MechanicalTeleporterLang mechanicalTeleporter = new MechanicalTeleporterLang();

    public static void register() {}

    public static class ModLangBuilder extends MechanicalRegistrateLangBuilder<CreateRegistrate> {
        public ModLangBuilder() {
            super(ModConstants.MODID, MechanicalTeleporter.registrate(),() -> new MechanicalLangBuilder(ModConstants.MODID));
        }
    }
}
