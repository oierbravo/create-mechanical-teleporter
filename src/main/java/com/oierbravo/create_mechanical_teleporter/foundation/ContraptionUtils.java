package com.oierbravo.create_mechanical_teleporter.foundation;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.lang.ref.Reference;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContraptionUtils {
    public static Stream<AbstractContraptionEntity> getIntersectionContraptionsStream(Level level, Entity entity) {
        return ContraptionHandler.loadedContraptions.get(level)
                .values()
                .stream()
                .map(Reference::get)
                .filter(cEntity -> cEntity != null && cEntity.collidingEntities.containsKey((Entity) entity));
    }

    public static Set<AbstractContraptionEntity> getIntersectingContraptions(Level level, Entity entity) {
        Set<AbstractContraptionEntity> contraptions = getIntersectionContraptionsStream(level, entity).collect(Collectors.toSet());

        contraptions.addAll(level.getEntitiesOfClass(AbstractContraptionEntity.class, (entity).getBoundingBox()
                .inflate(1f)));
        return contraptions;
    }
}
