package com.oierbravo.create_mechanical_teleporter.content.logistics.teleporters.mechanical;

import com.oierbravo.mechanicals.foundation.visual.QuarterShaftVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction;

public class TeleporterVisual extends QuarterShaftVisual<TeleporterBlockEntity> implements SimpleDynamicVisual {
    //private final RotatingInstance core;
    private final TeleporterBlockEntity teleporterBlockEntity;
    //private final OrientedInstance core;
    //private final SmartRecycler<TextureAtlasSprite, FluidInstance> fluidInstance;
    //private int light;

    public TeleporterVisual(VisualizationContext visualizationContext, TeleporterBlockEntity teleporterBlockEntity, float partialTick) {
        super(visualizationContext, teleporterBlockEntity, partialTick, Direction.DOWN);

        this.teleporterBlockEntity = teleporterBlockEntity;

        /*fluidInstance = new SmartRecycler<>(sprite -> visualizationContext.instancerProvider().instancer(AllInstanceTypes.FLUID, FluidMesh.stream(sprite))
                .createInstance());*/



        //ToDo: Figure out how to handle transparency
        /*

        boolean canWork = this.teleporterBlockEntity.checkRequerimentsForTeleport();
        PartialModel coreModel = (canWork) ? ModPartials.BLOCK_CORE_GLOW : ModPartials.BLOCK_CORE;


        core = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(ModPartials.BLOCK_CORE_GLOW)).createInstance();
        core.position(getVisualPosition()).setChanged();
        core.color(0,0,0,120);

        core.light(LightTexture.FULL_BRIGHT);
        transformModels(partialTick);*/
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        //transformModels(ctx.partialTick());


    }
    /*@Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        light = computePackedLight();
    }*/
    /*@Override
    protected void _delete() {
        super._delete();
        fluidInstance.delete();

    }*/
    /*private void transformModels(float pt) {
        float worldTime = AnimationTickHolder.getRenderTime() / 20;

        int direction = (blockEntity.getSpeed() >=0) ? 1 : -1;

        boolean canWork = this.teleporterBlockEntity.checkRequerimentsForTeleport();

        float floating = (canWork) ?  Mth.sin(worldTime) * .05f : 0;
        float angle = (canWork) ? direction * worldTime * -10 % 360 : 0;
        core.position(getVisualPosition()).rotateY(angle).translatePosition(0,floating,0).setChanged();
    }
    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(core);
    }

    @Override
    protected void _delete() {
        super._delete();
        core.delete();
    }
    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(core);
    }*/
}
