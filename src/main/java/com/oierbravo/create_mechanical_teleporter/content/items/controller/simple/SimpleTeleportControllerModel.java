package com.oierbravo.create_mechanical_teleporter.content.items.controller.simple;

import com.oierbravo.create_mechanical_teleporter.foundation.item.render.MechanicalTeleporterCustomRendererItemModel;
import com.simibubi.create.foundation.item.render.CreateCustomRenderedItemModel;
import net.minecraft.client.resources.model.BakedModel;

public class SimpleTeleportControllerModel extends MechanicalTeleporterCustomRendererItemModel {

	public SimpleTeleportControllerModel(BakedModel template) {
		super(template, "simple_teleport_controller");
		addPartials("powered", "button");
	}

}
