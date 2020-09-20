package studio.archetype.shutter;

import nerdhub.cardinal.components.api.event.WorldComponentCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import studio.archetype.shutter.components.Components;
import studio.archetype.shutter.entities.Entities;
import studio.archetype.shutter.pathing.CameraPathManager;

import java.util.HashMap;
import java.util.Map;

public class Shutter implements ModInitializer {

    public static final String MOD_ID = "shutter";

    public static Shutter INSTANCE;

    private Map<World, CameraPathManager> pathManagers = new HashMap<>();

    @Override
    public void onInitialize() {
        INSTANCE = this;
        Entities.register();

        ServerWorldEvents.LOAD.register((s, w) -> this.pathManagers.put(w, new CameraPathManager(w)));
        ServerWorldEvents.UNLOAD.register((s, w) -> this.pathManagers.remove(w));
    }


    public CameraPathManager getPathManager(World w) {
        return pathManagers.get(w);
    }

    public static Identifier id(String key) {
        return new Identifier(MOD_ID, key);
    }
}
