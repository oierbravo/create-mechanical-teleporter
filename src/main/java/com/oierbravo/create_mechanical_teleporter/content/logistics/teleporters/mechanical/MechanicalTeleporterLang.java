package com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters.mechanical;

import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.mechanicals.utility.lang.MechanicalLangBuilder;

public class MechanicalTeleporterLang extends ModLang.ModLangBuilder {
    public MechanicalLangBuilder summary = addBlockTooltipCondition("mechanical_teleporter",0, "When placed");
    public MechanicalLangBuilder condition0 = addBlockTooltipBehaviour("mechanical_teleporter",0, "Generates a new frequency or uses the tuned one");
    public MechanicalLangBuilder condition1 = addBlockTooltipCondition("mechanical_teleporter",1,"R-Click on another Teleporter");
    public MechanicalLangBuilder behaviour1 = addBlockTooltipBehaviour("mechanical_teleporter",1,"Tunes to the teleporter network");
}
