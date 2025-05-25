package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.fluid.EnderLiquidBlock;
import com.oierbravo.mechanicals.register.fluid.MechanicalSolidRenderedPlaceableFluidType;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;

import javax.annotation.Nullable;

public class ModFluids {
    public static final CreateRegistrate REGISTRATE = MechanicalTeleporter.registrate();
    public static final FluidEntry<BaseFlowingFluid.Flowing> ENDER_FLUID = REGISTRATE.standardFluid("ender_fluid", MechanicalSolidRenderedPlaceableFluidType.create(0x0b4d42,
                    () -> 1f / 8f * AllConfigs.client().honeyTransparencyMultiplier.getF()))
            .lang("Ender fluid")
            .properties(b -> b.viscosity(2000)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .source(BaseFlowingFluid.Source::new)
            .block(EnderLiquidBlock::new)
            .build()
            .bucket()
            .tag(AllTags.commonItemTag("buckets/ender_fluid"))
            .build()
            .register();

    public static void register() {}
    public static void registerFluidInteractions() {
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                ENDER_FLUID.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return Blocks.OBSIDIAN.defaultBlockState();
                    } else {
                        return Blocks.END_STONE
                                .defaultBlockState();
                    }
                }
        ));

    }

    @Nullable
    public static BlockState getLavaInteraction(FluidState fluidState) {
        Fluid fluid = fluidState.getType();

        if (fluid.isSame(ENDER_FLUID.get()))
            return Blocks.END_STONE.defaultBlockState();
        return null;
    }


}
