package studio.archetype.shutter.pathing;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
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

    public void addNode(Identifier cameraPathId, PathNode node) {
        CameraPath path = cameraPaths.computeIfAbsent(cameraPathId, CameraPath::new);
        path.addNode(node);
        MinecraftClient.getInstance().getToastManager().add(new ShutterMessageToast(
                ShutterMessageToast.Type.POSITIVE,
                new LiteralText("Node created!"),
                new LiteralText("Yes"),
                new LiteralText("no")
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

    public void togglePathVisualization(PlayerEntity e, Identifier id) {
        CameraPath path = cameraPaths.computeIfAbsent(id, CameraPath::new);

        if(path.getNodes().size() < 2) {
            e.sendMessage(new LiteralText("Not enough nodes, minimum 2."), true);
            return;
        }

        if(currentVisualization != null) {
            currentVisualization = null;
            ShutterClient.INSTANCE.getNodeRenderer().disable();
            ShutterClient.INSTANCE.getPathRenderer().disable();
            e.sendMessage(new LiteralText("Visualization for " + id.toString() + " destroyed."), true);

        } else {
            currentVisualization = id;
            ShutterClient.INSTANCE.getPathRenderer().setPath(path);
            ShutterClient.INSTANCE.getNodeRenderer().setPath(path);
            e.sendMessage(new LiteralText("Creating visualization for " + id.toString() + "."), true);
        }
    }
}
