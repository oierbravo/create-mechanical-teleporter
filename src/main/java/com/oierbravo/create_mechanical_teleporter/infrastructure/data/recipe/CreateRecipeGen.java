package com.oierbravo.create_mechanical_teleporter.infrastructure.data.recipe;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.registrate.ModFluids;
import com.oierbravo.create_mechanical_teleporter.registrate.ModItems;
import com.oierbravo.mechanicals.foundation.data.AbstractCreateRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

public class CreateRecipeGen extends AbstractCreateRecipeGen {
    public CreateRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, ModConstants::asResource);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        createSandPaperPolishing("polished_ender_crystal")
                .output(ModItems.POLISHED_ENDER_QUARTZ)
                .require(ModItems.ENDER_QUARTZ)
                .build(pRecipeOutput);

        createMixing("ender_fluid")
                .require(ModItems.POLISHED_ENDER_QUARTZ)
                .require(Fluids.LAVA,500)
                .output(ModItems.POLISHED_ENDER_QUARTZ)
                .output(ModFluids.ENDER_FLUID.get(),1000)
                .build(pRecipeOutput);

        createFilling("ender_soup")
                .require(Items.BOWL)
                .require(ModFluids.ENDER_FLUID.get().getSource(),500)
                .output(ModItems.ENDER_SOUP)
                .build(pRecipeOutput);

    }


}
