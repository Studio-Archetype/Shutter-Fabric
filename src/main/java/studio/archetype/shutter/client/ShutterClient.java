package studio.archetype.shutter.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.world.World;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.rendering.CameraNodeRenderer;
import studio.archetype.shutter.client.rendering.CameraPathRenderer;
import studio.archetype.shutter.pathing.CameraPathManager;

import java.util.HashMap;
import java.util.Map;

public class ShutterClient implements ClientModInitializer {

    public static ShutterClient INSTANCE;

    private InputHandler inputHandler;
    private CameraPathRenderer pathRenderer;
    private CameraNodeRenderer nodeRenderer;

    private Map<World, CameraPathManager> pathManagers;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        ClientConfigManager.loadConfig();
        this.inputHandler = new InputHandler();
        this.pathRenderer = new CameraPathRenderer();
        this.nodeRenderer = new CameraNodeRenderer();
        this.pathManagers = new HashMap<>();
    }

    public CameraPathManager getPathManager(World w) {
        return pathManagers.computeIfAbsent(w, world -> new CameraPathManager());
    }

    public CameraPathRenderer getPathRenderer() {
        return pathRenderer;
    }
    public CameraNodeRenderer getNodeRenderer() { return nodeRenderer; }
}
