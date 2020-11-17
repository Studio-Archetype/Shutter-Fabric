package studio.archetype.shutter.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
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

    private PathFollower follower;

    private Map<World, CameraPathManager> pathManagers;

    private double zoom, prevZoom;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        ClientConfigManager.register();
        this.inputHandler = new InputHandler();
        this.pathRenderer = new CameraPathRenderer();
        this.pathManagers = new HashMap<>();
        this.follower = new PathFollower();

    }

    public CameraPathManager getPathManager(World w) {
        return pathManagers.computeIfAbsent(w, world -> new CameraPathManager());
    }

    public CameraPathRenderer getPathRenderer() { return pathRenderer; }
    public PathFollower getPathFollower() { return follower; }

    public double getZoom(float delta) {
        if(zoom == 0 || prevZoom == 0)
            this.zoom = this.prevZoom = MinecraftClient.getInstance().options.fov;
        return MathHelper.lerp(delta, prevZoom, zoom);
    }

    public void setZoom(double zoom) {
        prevZoom = MinecraftClient.getInstance().options.fov;
        this.zoom = zoom;
    }

    public void setPreviousZoom(double prev) {
        this.prevZoom = prev;
    }
}
