package studio.archetype.shutter.client;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import studio.archetype.shutter.client.cmd.PathControlCommand;
import studio.archetype.shutter.client.cmd.PathNodeCommand;
import studio.archetype.shutter.client.cmd.PathVisualCommands;
import studio.archetype.shutter.client.cmd.handler.ClientCommandManager;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;
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

    private PathFollower follower;
    private PathIterator iterator;

    private Map<World, CameraPathManager> pathManagers;

    private double zoom, prevZoom;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        ClientConfigManager.register();

        this.inputHandler = new InputHandler();
        this.pathRenderer = new CameraPathRenderer();
        this.nodeRenderer = new CameraNodeRenderer();
        this.pathManagers = new HashMap<>();
        this.follower = new PathFollower();
        this.iterator = new PathIterator();

        CommandDispatcher<FabricClientCommandSource> dis = ClientCommandManager.DISPATCHER;
        PathControlCommand.register(dis);
        PathNodeCommand.register(dis);
        PathVisualCommands.register(dis);

        this.zoom = this.prevZoom = 0;
    }

    public CameraPathManager getPathManager(World w) {
        return pathManagers.computeIfAbsent(w, world -> new CameraPathManager());
    }

    public CameraPathRenderer getPathRenderer() { return pathRenderer; }
    public CameraNodeRenderer getNodeRenderer() { return nodeRenderer; }
    public PathFollower getPathFollower() { return follower; }
    public PathIterator getPathIterator() { return iterator; }

    public double getZoom() {
        return this.zoom;
    }

    public double getCurrentZoomModifier() {
        return this.prevZoom;
    }

    public double getZoom(float delta) {
        return (prevZoom = MathHelper.lerp(delta, prevZoom, zoom));
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public void resetZoom() {
        this.zoom = this.prevZoom = 0;
    }
}
