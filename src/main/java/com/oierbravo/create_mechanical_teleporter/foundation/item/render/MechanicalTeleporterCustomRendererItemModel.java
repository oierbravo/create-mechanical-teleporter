package com.oierbravo.create_mechanical_teleporter.foundation.item.render;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import net.minecraft.client.resources.model.BakedModel;

public class MechanicalTeleporterCustomRendererItemModel extends CustomRenderedItemModel {
    public MechanicalTeleporterCustomRendererItemModel(BakedModel template, String basePath) {
        super(template, MechanicalTeleporter.MODID, basePath);
    }
}
