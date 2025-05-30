package com.oierbravo.create_mechanical_teleporter.registrate;

import com.oierbravo.create_mechanical_teleporter.MechanicalTeleporter;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterBlock;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterBlockItem;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterInteractionBehaviour;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.TeleporterMovementBehaviour;
import com.oierbravo.create_mechanical_teleporter.content.kinetics.mechanical_teleporter.creative.CreativeTeleporterBlock;
import com.oierbravo.create_mechanical_teleporter.content.logistics.manager.TeleporterManagerBlock;
import com.oierbravo.create_mechanical_teleporter.infrastructure.config.ModStress;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.redstone.RoseQuartzLampBlock;
import com.simibubi.create.foundation.data.*;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

import static com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour.interactionBehaviour;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

@SuppressWarnings("removal")
public class ModBlocks {


    private static final CreateRegistrate REGISTRATE = MechanicalTeleporter.registrate();

    public static final BlockEntry<TeleporterBlock> MECHANICAL_TELEPORTER = REGISTRATE.block("mechanical_teleporter", TeleporterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.METAL))
            .properties(p -> p.lightLevel($ -> 5))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .addLayer(() -> RenderType::translucent)
            .transform(ModStress.setImpact(8.0))
            .blockstate((ctx, prov) ->
                    prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
                        String modelFileName = "create_mechanical_teleporter:block/mechanical_teleporter/block";
                        if (state.getValue(BlockStateProperties.POWERED))
                            modelFileName += "_powered";
                        if (state.getValue(TeleporterBlock.ACTIVE))
                            modelFileName += "_active";
                        return ConfiguredModel.builder().modelFile(prov.models().getExistingFile(ResourceLocation.parse(modelFileName)))
                                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360).build();

                    })
            )
            .onRegister(movementBehaviour(new TeleporterMovementBehaviour()))
            .onRegister(interactionBehaviour(new TeleporterInteractionBehaviour()))
            .item(TeleporterBlockItem::new)
            .transform(customItemModel("mechanical_teleporter", "item"))
            .register();

    public static final BlockEntry<CreativeTeleporterBlock> CREATIVE_TELEPORTER = REGISTRATE.block("creative_teleporter", CreativeTeleporterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.METAL))
            .properties(p -> p.lightLevel($ -> 5))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .onRegister(movementBehaviour(new TeleporterMovementBehaviour()))
            .onRegister(interactionBehaviour(new TeleporterInteractionBehaviour()))
            .transform(pickaxeOnly())
            .addLayer(() -> RenderType::translucent)
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .item(TeleporterBlockItem::new)
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<TeleporterManagerBlock> TELEPORTER_MANAGER = REGISTRATE.block("teleporter_manager", TeleporterManagerBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.METAL))
            .properties(p -> p.lightLevel($ -> 5))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .addLayer(() -> RenderType::translucent)
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .item()
            .transform(customItemModel("teleporter_manager", "item"))
            .register();


    public static final BlockEntry<RoseQuartzLampBlock> ENDER_QUARTZ_LAMP =
            REGISTRATE.block("ender_quartz_lamp", RoseQuartzLampBlock::new)
                    .initialProperties(() -> Blocks.REDSTONE_LAMP)
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_CYAN)
                            .lightLevel(s -> s.getValue(RoseQuartzLampBlock.POWERING) ? 15 : 0))
                    .blockstate((c, p) -> BlockStateGen.simpleBlock(c, p, s -> {
                        boolean powered = s.getValue(RoseQuartzLampBlock.POWERING);
                        String name = c.getName() + (powered ? "_powered" : "");
                        return p.models()
                                .cubeAll(name, p.modLoc("block/" + name));
                    }))
                    .transform(pickaxeOnly())
                    .simpleItem()
                    .register();

    public static final BlockEntry<CasingBlock> ENDER_CASING = REGISTRATE.block("ender_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_CYAN))
            .transform(BuilderTransformers.casing(() -> ModSpriteShifts.ENDER_CASING))
            .lang("Ender Casing")
            .register();

    public static final BlockEntry<CasingBlock> ENDER_CREATIVE_CASING = REGISTRATE.block("ender_creative_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_CYAN))
            .transform(BuilderTransformers.casing(() -> ModSpriteShifts.ENDER_CREATIVE_CASING))
            .lang("Ender Creative Casing")
            .register();

    public static void register() {}
}
