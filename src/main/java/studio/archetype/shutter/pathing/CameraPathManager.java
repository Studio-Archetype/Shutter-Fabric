package studio.archetype.shutter.pathing;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.PathFollower;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.ui.ShutterMessageToast;

import java.util.*;

public class CameraPathManager {

    public static final Identifier DEFAULT_PATH = Shutter.id("default");

    private final Map<Identifier, CameraPath> cameraPaths = new HashMap<>();
    private Identifier currentVisualization;

    public CameraPathManager() {
        cameraPaths.put(DEFAULT_PATH, new CameraPath(DEFAULT_PATH));
    }

    public List<CameraPath> getPaths() {
        return Lists.newArrayList(cameraPaths.values());
    }

    public CameraPath getPath(Identifier id) {
        return cameraPaths.computeIfAbsent(id, CameraPath::new);
    }

    public boolean isVisualized(Identifier id) {
        return id.equals(currentVisualization);
    }

    public void addNode(Identifier cameraPathId, PathNode node) {
        CameraPath path = cameraPaths.computeIfAbsent(cameraPathId, CameraPath::new);
        path.addNode(node);
        MinecraftClient.getInstance().getToastManager().add(new ShutterMessageToast(
                ShutterMessageToast.Type.POSITIVE,
                new LiteralText("Node Created @"),
                new LiteralText("x").formatted(Formatting.DARK_RED)
                        .append(new LiteralText(String.format("%.3f", node.getPosition().x)).formatted(Formatting.RED, Formatting.UNDERLINE))
                        .append(new LiteralText(" y").formatted(Formatting.DARK_GREEN))
                        .append(new LiteralText(String.format("%.3f", node.getPosition().y)).formatted(Formatting.GREEN, Formatting.UNDERLINE))
                        .append(new LiteralText(" z").formatted(Formatting.DARK_BLUE))
                        .append(new LiteralText(String.format("%.3f", node.getPosition().z)).formatted(Formatting.BLUE, Formatting.UNDERLINE))
        ));
    }

    public void startCameraPath(Identifier id) {
        CameraPath path = cameraPaths.computeIfAbsent(id, CameraPath::new);

        if(path.getNodes().size() < 2 || MinecraftClient.getInstance().player == null) {
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("Needs more than 2 nodes."), true);
            return;
        }

        PathFollower follower = ShutterClient.INSTANCE.getPathFollower();

        if(follower.isFollowing()) {
            follower.end();
            return;
        }
        follower.start(ShutterClient.INSTANCE.getPathManager(MinecraftClient.getInstance().world).getPaths().get(0));
    }

    public void clearPath(Identifier id) {
        CameraPath path = cameraPaths.computeIfAbsent(id, CameraPath::new);
        if(path.getNodes().isEmpty()) {
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("Path is empty already."), true);
            return;
        }

        if(ShutterClient.INSTANCE.getPathFollower().isFollowing())
            ShutterClient.INSTANCE.getPathFollower().end();
        if(currentVisualization != null)
            togglePathVisualization(MinecraftClient.getInstance().player, id);

        path.clear();
    }

    public void togglePathVisualization(PlayerEntity e, Identifier id) {
        CameraPath path = cameraPaths.computeIfAbsent(id, CameraPath::new);

        if(path.getNodes().size() < 2) {
            e.sendMessage(new LiteralText("Not enough nodes, minimum 2."), true);
            return;
        }

        if(currentVisualization != null) {
            currentVisualization = null;
            ShutterClient.INSTANCE.getPathRenderer().disable();
            ShutterClient.INSTANCE.getNodeRenderer().setPath(null);
            e.sendMessage(new LiteralText("Visualization for " + id.toString() + " destroyed."), true);
        } else {
            currentVisualization = id;
            ShutterClient.INSTANCE.getPathRenderer().setPath(path);
            ShutterClient.INSTANCE.getNodeRenderer().setPath(path);
            e.sendMessage(new LiteralText("Creating visualization for " + id.toString() + "."), true);
        }
    }
}
