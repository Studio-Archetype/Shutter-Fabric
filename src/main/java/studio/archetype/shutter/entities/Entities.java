package studio.archetype.shutter.entities;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public final class Entities {

    public static EntityType<CameraPointEntity> ENTITY_CAMERA_POINT;

    public static void register() {
        ENTITY_CAMERA_POINT = Registry.register(
                Registry.ENTITY_TYPE,
                CameraPointEntity.ENTITY_ID,
                FabricEntityTypeBuilder
                        .<CameraPointEntity>create(SpawnGroup.MISC, CameraPointEntity::new)
                        .dimensions(EntityDimensions.fixed(0.75F, 0.75F))
                        .spawnableFarFromPlayer()
                        .disableSaving()
                        .disableSummon()
                        .fireImmune()
                .build()
        );
    }
}
