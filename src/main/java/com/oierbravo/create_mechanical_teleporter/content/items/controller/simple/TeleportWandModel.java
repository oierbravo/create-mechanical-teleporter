package com.oierbravo.create_mechanical_teleporter.content.items.controller.simple;

import com.oierbravo.create_mechanical_teleporter.foundation.item.render.MechanicalTeleporterCustomRendererItemModel;
import net.minecraft.client.resources.model.BakedModel;

public class TeleportWandModel extends MechanicalTeleporterCustomRendererItemModel {

	public TeleportWandModel(BakedModel template) {
		super(template, "teleport_wand");
		//addPartials("powered", "button");
	}

}
