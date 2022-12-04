package com.oierbravo.create_mechanical_teleporter.content.items.controller.multiple;

import com.simibubi.create.foundation.item.render.CreateCustomRenderedItemModel;
import net.minecraft.client.resources.model.BakedModel;

public class LinkedTeleportControllerModel extends CreateCustomRenderedItemModel {

	public LinkedTeleportControllerModel(BakedModel template) {
		super(template, "linked_controller");
		addPartials("powered", "button");
	}

}
