package com.oierbravo.create_mechanical_teleporter.infrastructure.data;

import com.oierbravo.create_mechanical_teleporter.ModConstants;
import com.oierbravo.create_mechanical_teleporter.registrate.ModPotatoProjectileTypes;
import com.simibubi.create.api.registry.CreateRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CreateRegistriesEntriesProvider extends DatapackBuiltinEntriesProvider {
	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
		.add(CreateRegistries.POTATO_PROJECTILE_TYPE, ModPotatoProjectileTypes::bootstrap);

	public CreateRegistriesEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, BUILDER, Set.of(ModConstants.MODID));
	}

	@Override
	public String getName() {
		return "Create Mechanical teleporter's Create Registry Entries";
	}
}