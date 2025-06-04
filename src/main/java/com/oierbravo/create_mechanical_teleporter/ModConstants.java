package com.oierbravo.create_mechanical_teleporter;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ModConstants {
    public static final String MODID = "create_mechanical_teleporter";
    public static final String DISPLAY_NAME = "Create Mechanical Teleporter";
    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
    public static ResourceLocation asResource() {
        return asResource("");
    }
    public static Supplier<ModLang.ModLangBuilder> langBuilder(){
        return ModLang.ModLangBuilder::new;
    };
}
