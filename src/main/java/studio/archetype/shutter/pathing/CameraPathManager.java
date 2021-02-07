package studio.archetype.shutter.pathing;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.camera.PathFollower;
import studio.archetype.shutter.client.ui.Messaging;
import studio.archetype.shutter.pathing.exceptions.PathEmptyException;
import studio.archetype.shutter.pathing.exceptions.PathNotFollowingException;
import studio.archetype.shutter.pathing.exceptions.PathTooSmallException;
import studio.archetype.shutter.util.SerializationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraPathManager {

    public static final Identifier DEFAULT_PATH = Shutter.id("default");

    private final Map<Identifier, CameraPath> cameraPaths;
    private Identifier currentSelection;
    private boolean isVisualizing;

    public CameraPathManager() {
        this(new HashMap<>(), DEFAULT_PATH);
        cameraPaths.put(DEFAULT_PATH, new CameraPath(DEFAULT_PATH));
    }

    private CameraPathManager(Map<Identifier, CameraPath> paths, Identifier selection) {
        this.cameraPaths = new HashMap<>(paths);
        this.currentSelection = selection;
        this.isVisualizing = false;
    }

    public List<CameraPath> getPaths() {
        return Lists.newArrayList(cameraPaths.values());
    }

    public CameraPath getCurrentPath() { return getPath(currentSelection); }

    public void setCurrentPath(Identifier id) {
        CameraPath path = getPath(id);

        this.currentSelection = id;
        if(isVisualizing) {
            ShutterClient.INSTANCE.getPathRenderer().setPath(path);
            ShutterClient.INSTANCE.getNodeRenderer().setPath(path);
        }
    }

    public CameraPath getPath(Identifier id) {
        return cameraPaths.computeIfAbsent(id, CameraPath::new);
    }

    public boolean isVisualizing() { return isVisualizing; }

    public void addNode(Identifier cameraPathId, PathNode node) {
        CameraPath path = cameraPaths.computeIfAbsent(cameraPathId, CameraPath::new);
        path.addNode(node);
        Messaging.sendMessage(
                new TranslatableText("msg.shutter.headline.cmd.success"),
                new TranslatableText("msg.shutter.ok.add_node", path.getNodes().indexOf(node)),
                new LiteralText("x").formatted(Formatting.DARK_RED)
                    .append(new LiteralText(String.format("%.2f", node.getPosition().x)).formatted(Formatting.RED, Formatting.UNDERLINE))
                    .append(new LiteralText(" y").formatted(Formatting.DARK_GREEN))
                    .append(new LiteralText(String.format("%.2f", node.getPosition().y)).formatted(Formatting.GREEN, Formatting.UNDERLINE))
                    .append(new LiteralText(" z").formatted(Formatting.DARK_BLUE))
                    .append(new LiteralText(String.format("%.2f", node.getPosition().z)).formatted(Formatting.BLUE, Formatting.UNDERLINE)),
                Messaging.MessageType.POSITIVE);
    }

    public void startCameraPath(double pathTime) throws PathTooSmallException {
        CameraPath path = getCurrentPath();

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
        CameraPath path = getPath(id);
        if(path.getNodes().isEmpty())
            throw new PathEmptyException();

        if(this.currentSelection == id) {
            this.currentSelection = DEFAULT_PATH;

            if(ShutterClient.INSTANCE.getPathFollower().isFollowing())
                ShutterClient.INSTANCE.getPathFollower().end();
            if(ShutterClient.INSTANCE.getPathIterator().isIterating())
                ShutterClient.INSTANCE.getPathIterator().end();

            if(isVisualizing) {
                this.isVisualizing = false;
                ShutterClient.INSTANCE.getPathRenderer().disable();
                ShutterClient.INSTANCE.getNodeRenderer().disable();
            }
        } else {
            cameraPaths.remove(id);
        }

        path.clear();
    }

    public boolean togglePathVisualization() throws PathTooSmallException {
        CameraPath path = getCurrentPath();

        if(path.getNodes().size() < 2)
            throw new PathTooSmallException();

        if(isVisualizing) {
            isVisualizing = false;
            ShutterClient.INSTANCE.getPathRenderer().disable();
            ShutterClient.INSTANCE.getNodeRenderer().disable();
            return false;
        } else {
            isVisualizing = true;
            ShutterClient.INSTANCE.getPathRenderer().setPath(path);
            ShutterClient.INSTANCE.getNodeRenderer().setPath(path);
            return true;
        }
    }

    public static Codec<CameraPathManager> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                Codec.unboundedMap(SerializationUtils.CODEC_IDENTIFIER, CameraPath.CODEC).fieldOf("paths").forGetter((CameraPathManager o) -> o.cameraPaths),
                SerializationUtils.CODEC_IDENTIFIER.fieldOf("selection").forGetter((CameraPathManager o) -> o.currentSelection)
            ).apply(i, CameraPathManager::new));
 }
