package com.oierbravo.create_mechanical_teleporter.content.logistics.manager;

import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.mechanicals.utility.lang.MechanicalLangBuilder;

public class TeleporterManagerLang extends ModLang.ModLangBuilder {


    public MechanicalLangBuilder title = add("teleporter_manager.title", "Teleporter manager");
    public MechanicalLangBuilder guiDimension = add("gui.teleporter_manager.dimension", "Dimension: %s");
    public MechanicalLangBuilder guiPos = add("gui.teleporter_manager.pos", "Pos: %s");

}
