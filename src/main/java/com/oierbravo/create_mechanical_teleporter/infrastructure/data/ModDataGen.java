package com.oierbravo.create_mechanical_teleporter.infrastructure.data;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.infrastructure.data.recipe.CreateRecipeGen;
import com.tterrag.registrate.providers.RegistrateDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static com.oierbravo.create_mechanical_teleporter.ModConstants.MODID;

public class ModDataGen {
    public static void gatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        CreateRegistriesEntriesProvider generatedEntriesProvider = new CreateRegistriesEntriesProvider(output, lookupProvider);
        lookupProvider = generatedEntriesProvider.getRegistryProvider();
        generator.addProvider(event.includeServer(), generatedEntriesProvider);

        if (event.includeServer()) {
            generator.addProvider(true, new CreateRecipeGen(output, lookupProvider));

        }
        event.getGenerator().addProvider(true, MechanicalTeleporter.registrate().setDataProvider(new RegistrateDataProvider(MechanicalTeleporter.registrate(), MODID, event)));

    }
}
