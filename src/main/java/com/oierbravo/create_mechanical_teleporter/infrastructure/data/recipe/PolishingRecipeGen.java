package com.oierbravo.create_mechanical_teleporter.infrastructure.data.recipe;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.oierbravo.mechanicals.foundation.data.AbstractCreateRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.concurrent.CompletableFuture;

public class PolishingRecipeGen extends AbstractCreateRecipeGen {
    public PolishingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, ModConstants::asResource);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        createSandPaperPolishing("polished_ender_crystal")
                .output(ModItems.POLISHED_ENDER_QUARTZ)
                .require(ModItems.ENDER_QUARTZ)
                .build(pRecipeOutput);
    }


}
