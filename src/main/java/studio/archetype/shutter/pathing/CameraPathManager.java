package studio.archetype.shutter.pathing;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.PathFollower;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.ui.ShutterMessageToast;
import studio.archetype.shutter.pathing.exceptions.PathEmptyException;
import studio.archetype.shutter.pathing.exceptions.PathNotFollowingException;
import studio.archetype.shutter.pathing.exceptions.PathTooSmallException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public boolean isVisualizing() { return this.currentVisualization != null; }

    public void addNode(Identifier cameraPathId, PathNode node) {
        CameraPath path = cameraPaths.computeIfAbsent(cameraPathId, CameraPath::new);
        path.addNode(node);
        MinecraftClient.getInstance().getToastManager().add(new ShutterMessageToast(
                ShutterMessageToast.Type.POSITIVE,
                new LiteralText(String.format("Node #%d Created", path.getNodes().indexOf(node))),
                new LiteralText("x").formatted(Formatting.DARK_RED)
                        .append(new LiteralText(String.format("%.3f", node.getPosition().x)).formatted(Formatting.RED, Formatting.UNDERLINE))
                        .append(new LiteralText(" y").formatted(Formatting.DARK_GREEN))
                        .append(new LiteralText(String.format("%.3f", node.getPosition().y)).formatted(Formatting.GREEN, Formatting.UNDERLINE))
                        .append(new LiteralText(" z").formatted(Formatting.DARK_BLUE))
                        .append(new LiteralText(String.format("%.3f", node.getPosition().z)).formatted(Formatting.BLUE, Formatting.UNDERLINE))
        ));
    }

    public void startCameraPath(Identifier id, double pathTime) throws PathTooSmallException {
        CameraPath path = cameraPaths.computeIfAbsent(id, CameraPath::new);

        if(path.getNodes().size() < 2 || MinecraftClient.getInstance().player == null)
            throw new PathTooSmallException();

        ShutterClient.INSTANCE.getPathFollower().start(path, pathTime);
    }

    public void stopCameraPath() throws PathNotFollowingException {
        PathFollower follower = ShutterClient.INSTANCE.getPathFollower();

        if(follower.isFollowing())
            follower.end();
        else
            throw new PathNotFollowingException();
    }

    public void clearPath(Identifier id) throws PathEmptyException {
        CameraPath path = cameraPaths.computeIfAbsent(id, CameraPath::new);
        if(path.getNodes().isEmpty())
            throw new PathEmptyException();

        if(ShutterClient.INSTANCE.getPathFollower().isFollowing())
            ShutterClient.INSTANCE.getPathFollower().end();
        if(ShutterClient.INSTANCE.getPathIterator().isIterating())
            ShutterClient.INSTANCE.getPathIterator().end();

        this.currentVisualization = null;
        ShutterClient.INSTANCE.getPathRenderer().disable();
        ShutterClient.INSTANCE.getNodeRenderer().disable();

        path.clear();
    }

    public boolean togglePathVisualization(Identifier id) throws PathTooSmallException {
        CameraPath path = cameraPaths.computeIfAbsent(id, CameraPath::new);

        if(path.getNodes().size() < 2)
            throw new PathTooSmallException();

        if(currentVisualization != null) {
            currentVisualization = null;
            ShutterClient.INSTANCE.getPathRenderer().disable();
            ShutterClient.INSTANCE.getNodeRenderer().disable();
            return false;
        } else {
            currentVisualization = id;
            ShutterClient.INSTANCE.getPathRenderer().setPath(path);
            ShutterClient.INSTANCE.getNodeRenderer().setPath(path);
            return true;
        }
    }
}
