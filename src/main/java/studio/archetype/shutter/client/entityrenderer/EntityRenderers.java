package studio.archetype.shutter.client.entityrenderer;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import studio.archetype.shutter.entities.CameraPointEntity;
import studio.archetype.shutter.entities.Entities;

public class EntityRenderers {

    public static void register() {
        EntityRendererRegistry.INSTANCE.register(Entities.ENTITY_CAMERA_POINT, (d, ctx) -> new CameraPointEntityRenderer(d));
    }
}
