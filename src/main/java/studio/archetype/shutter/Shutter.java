package studio.archetype.shutter;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import nerdhub.cardinal.components.api.ComponentRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.entities.Entities;
import studio.archetype.shutter.pathing.CameraPathManager;

public class Shutter implements ModInitializer, WorldComponentInitializer {

    public static final String MOD_ID = "shutter";

    public static Shutter INSTANCE;

    public static final ComponentKey<CameraPathManager> PATH_COMPONENT =
            ComponentRegistry.INSTANCE.registerStatic(id("path_manager"), CameraPathManager.class);

    @Override
    public void onInitialize() {
        INSTANCE = this;
        NetworkHandler.register();
        Entities.register();
    }

    public static Identifier id(String key) {
        return new Identifier(MOD_ID, key);
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry reg) {
        reg.register(PATH_COMPONENT, CameraPathManager::new);
    }
}
