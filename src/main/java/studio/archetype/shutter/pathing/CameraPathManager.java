package studio.archetype.shutter.pathing;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.ui.ShutterMessageToast;

import java.util.*;
import java.util.stream.Collectors;

public class CameraPathManager {

    public static final Identifier DEFAULT_PATH = Shutter.id("default");

    private final Map<Identifier, CameraPath> cameraPaths = new HashMap<>();
    private Identifier currentVisualization;

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

    public void togglePathVisualization(PlayerEntity e, Identifier id) {
        CameraPath path = cameraPaths.computeIfAbsent(id, CameraPath::new);

        if(path.getNodes().size() < 2) {
            e.sendMessage(new LiteralText("Not enough nodes, minimum 2."), true);
            return;
        }

        if(currentVisualization != null) {
            currentVisualization = null;
            ShutterClient.INSTANCE.getNodeRenderer().resetCameraPath();
            ShutterClient.INSTANCE.getPathRenderer().resetPoints();
            e.sendMessage(new LiteralText("Visualization for " + id.toString() + " destroyed."), true);

        } else {
            currentVisualization = id;
            ShutterClient.INSTANCE.getPathRenderer().setPoints(new LinkedList<>(path.getNodes().stream().map(PathNode::getPosition).collect(Collectors.toList())));
            ShutterClient.INSTANCE.getNodeRenderer().setCameraPath(path);
            e.sendMessage(new LiteralText("Creating visualization for " + id.toString() + "."), true);
        }
    }
}
