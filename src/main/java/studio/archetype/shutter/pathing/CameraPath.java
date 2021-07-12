package studio.archetype.shutter.pathing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.pathing.exceptions.PathSerializationException;
import studio.archetype.shutter.pathing.interpolation.HermiteInterpolator;
import studio.archetype.shutter.pathing.interpolation.Interpolator;
import studio.archetype.shutter.pathing.interpolation.LinearInterpolator;
import studio.archetype.shutter.util.SerializationUtils;
import studio.archetype.shutter.util.WebUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class CameraPath {

    public final Identifier id;
    private final LinkedList<PathNode> nodes;

    private final Map<PathNode, LinkedList<InterpolationData>> interpolation = new HashMap<>();
    private final Map<PathNode, LinkedList<InterpolationData>> loopedInterpolation = new HashMap<>();
    private boolean needsInterpolationRebuilt, needsLoopedRebuilt;

    public CameraPath(Identifier id) {
        this(id, new LinkedList<>());
    }

    private CameraPath(Identifier id, LinkedList<PathNode> nodes) {
        this.id = id;
        this.nodes = nodes;
        this.needsInterpolationRebuilt = this.needsLoopedRebuilt = true;
    }

    public Identifier getId() {
        return this.id;
    }

    public LinkedList<PathNode> getNodes() {
        return nodes;
    }

    public void addNode(PathNode node) {
        this.nodes.add(node);
        needsInterpolationRebuilt = this.needsLoopedRebuilt = true;
        ShutterClient.INSTANCE.getSaveFile().save();
    }

    public void setNode(PathNode node, int index) throws IndexOutOfBoundsException {
        if(index >= nodes.size())
            throw new IndexOutOfBoundsException();

        nodes.set(index, node);
        needsInterpolationRebuilt = this.needsLoopedRebuilt = true;
        ShutterClient.INSTANCE.getSaveFile().save();
    }

    public void removeNode(int index) throws IndexOutOfBoundsException {
        if(index >= nodes.size())
            throw new IndexOutOfBoundsException();

        nodes.remove(index);
        needsInterpolationRebuilt = this.needsLoopedRebuilt = true;
        ShutterClient.INSTANCE.getSaveFile().save();
    }

    public Map<PathNode, LinkedList<InterpolationData>> getInterpolatedData(boolean looped) {
        if(looped) {
            if(needsLoopedRebuilt)
                calculatePath(true);
            return loopedInterpolation;
        } else {
            if(needsInterpolationRebuilt)
                calculatePath(false);
            return interpolation;
        }
    }

    public void calculatePath(boolean looped) {
        if(looped)
            loopedInterpolation.clear();
        else
            interpolation.clear();

        LinkedList<PathNode> fixedNodes = fixYaw();
        Interpolator interpolator = new LinearInterpolator(fixedNodes, looped);

        for(int i = 0; i < nodes.size() - (looped ? 0 : 1); i++) {
            LinkedList<InterpolationData> splinePoints = new LinkedList<>();
            if(i == 1)
                interpolator = new HermiteInterpolator(fixedNodes, looped);

            for(float ii = 0; ii < 1; ii += ClientConfigManager.CLIENT_CONFIG.genSettings.curveDetail.detail)
                splinePoints.add(interpolator.interpolate(i, ii));
            if(i != nodes.size() - 1)
                splinePoints.add(new InterpolationData(fixedNodes.get(i + 1)));

            if(looped)
                loopedInterpolation.put(nodes.get(i), splinePoints);
            else
                interpolation.put(nodes.get(i), splinePoints);
        }

        if(looped)
            needsLoopedRebuilt = false;
        else
            needsInterpolationRebuilt = false;
    }

    public void clear() {
        nodes.clear();
        interpolation.clear();
        needsInterpolationRebuilt = false;
    }

    public boolean export(String filename) {
        try {
            return ShutterClient.INSTANCE.getSaveFile().exportJson(filename, toJson(filename));
        } catch(PathSerializationException e) {
            System.out.println("Failed to encode path data!");
            return false;
        }
    }

    public boolean exportHastebin(String filename) {
        try {
            WebUtils.createPaste(toJson(filename).toString());
            return true;
        } catch(PathSerializationException e) {
            return false;
        }
    }

    public void offset(Vec3d origin) {
        LinkedList<PathNode> newNodes = new LinkedList<>();
        PathNode previous = nodes.get(0);
        for(int i = 0; i < nodes.size(); i++) {
            PathNode o = nodes.get(i);
            if(i == 0) {
                newNodes.add(new PathNode(origin, o.getPitch(), o.getYaw(), o.getRoll(), o.getZoom()));
                continue;
            }
            Vec3d offset = o.getPosition().subtract(previous.getPosition());
            newNodes.add(new PathNode(origin.add(offset), o.getPitch(), o.getYaw(), o.getRoll(), o.getZoom()));
        }

        nodes.clear();
        nodes.addAll(newNodes);

        this.needsInterpolationRebuilt = this.needsLoopedRebuilt = true;
    }

    private LinkedList<PathNode> fixYaw() {
        LinkedList<PathNode> fixedNodes = new LinkedList<>();
        fixedNodes.add(nodes.get(0));

        for(int i = 0; i < nodes.size() - 1; i++) {
            PathNode end = nodes.get(i + 1);
            float startYaw = fixedNodes.get(i).getYaw();
            float endYaw = end.getYaw();
            float invertedEnd = endYaw < 0 ? endYaw + 360 : endYaw - 360;

            float upwards = Math.abs(Math.abs(endYaw) - Math.abs(startYaw));
            float downwards = Math.abs(invertedEnd - startYaw);

            if(downwards < upwards)
                endYaw = invertedEnd;

            fixedNodes.add(new PathNode(end.getPosition(), end.getPitch(), endYaw, end.getRoll(), end.getZoom()));
        }

        return fixedNodes;
    }

    private JsonElement toJson(String filename) throws PathSerializationException {
        Optional<JsonElement> json = JsonOps.INSTANCE.withEncoder(CODEC).apply(this).resultOrPartial(System.out::println);
        if(json.isPresent()) {
            JsonObject obj = json.get().getAsJsonObject();
            obj.addProperty("Id", Shutter.id(filename).toString());
            return obj;
        } else
            throw new PathSerializationException("");
    }

    public static final Codec<CameraPath> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    SerializationUtils.CODEC_IDENTIFIER.fieldOf("Id").forGetter(CameraPath::getId),
                    PathNode.CODEC.listOf().fieldOf("Nodes").forGetter(CameraPath::getNodes))
                    .apply(i, (id, nodes) -> new CameraPath(id, new LinkedList<>(nodes))));
}
