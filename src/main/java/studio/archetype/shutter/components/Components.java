package studio.archetype.shutter.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import studio.archetype.shutter.Shutter;

public class Components implements WorldComponentInitializer {

    public static final ComponentKey<PathComponent> PATH_COMPONENT =
            ComponentRegistryV3.INSTANCE.getOrCreate(Shutter.id("path"), PathComponent.class);

    public void registerWorldComponentFactories(WorldComponentFactoryRegistry reg) {
        reg.register(PATH_COMPONENT, w -> new CameraPathComponent());
    }
}
