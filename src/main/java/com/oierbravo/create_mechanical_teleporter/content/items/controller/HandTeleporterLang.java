package com.oierbravo.create_mechanical_teleporter.content.items.controller;

import com.oierbravo.create_mechanical_teleporter.ModLang;
import com.oierbravo.mechanicals.utility.lang.MechanicalLangBuilder;

public class HandTeleporterLang extends ModLang.ModLangBuilder {


    public MechanicalLangBuilder tooltip_clear = add("hand_teleporter.tooltip.clear", "Hold shift + right click to clear");
    public MechanicalLangBuilder tooltip_address = add("hand_teleporter.tooltip.address", "Address: %s");
    public MechanicalLangBuilder message_cleared = add("hand_teleporter.message.cleared", "Frequency cleared");
    public MechanicalLangBuilder summary = addItemTooltipSummary("hand_teleporter", "Holds one frequency");
    public MechanicalLangBuilder condition1 = addItemTooltipCondition("hand_teleporter",1,"When used");
    public MechanicalLangBuilder behaviour1 = addItemTooltipBehaviour("hand_teleporter",1,"Teleports to the __configured frequency__");
    public MechanicalLangBuilder condition2 = addItemTooltipCondition("hand_teleporter",2,"R-Click on Teleporter");
    public MechanicalLangBuilder behaviour2 = addItemTooltipBehaviour("hand_teleporter",2,"Tunes to the __teleporter__ network");
    public MechanicalLangBuilder condition3 = addItemTooltipCondition("hand_teleporter",3,"R-Click while Sneaking");
    public MechanicalLangBuilder behaviour4 = addItemTooltipBehaviour("hand_teleporter",3,"__Clears__ the frequency");

}
