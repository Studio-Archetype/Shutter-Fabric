package studio.archetype.shutter.pathing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.pathing.exceptions.PathSerializationException;
import studio.archetype.shutter.util.InterpolationMath;
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
    private boolean needsInterpolationRebuilt = true;

    public CameraPath(Identifier id) {
        this(id, new LinkedList<>());
    }

    private CameraPath(Identifier id, LinkedList<PathNode> nodes) {
        this.id = id;
        this.nodes = nodes;
    }

    public Identifier getId() {
        return this.id;
    }

    public LinkedList<PathNode> getNodes() {
        return nodes;
    }

    public void addNode(PathNode node) {
        this.nodes.add(node);
        needsInterpolationRebuilt = true;
    }

    public void setNode(PathNode node, int index) throws IndexOutOfBoundsException {
        if(index >= nodes.size())
            throw new IndexOutOfBoundsException();

        nodes.set(index, node);
        this.needsInterpolationRebuilt = true;
    }

    public void removeNode(int index) throws IndexOutOfBoundsException {
        if(index >= nodes.size())
            throw new IndexOutOfBoundsException();

        nodes.remove(index);
        this.needsInterpolationRebuilt = true;
    }

    public Map<PathNode, LinkedList<InterpolationData>> getInterpolatedData() {
        if(needsInterpolationRebuilt)
            calculatePath();
        return interpolation;
    }

    public void calculatePath() {
        interpolation.clear();
        for (int i = 0; i < nodes.size() - 1; i++) {
            PathNode start = getWrapped(i, 0);
            PathNode end = getWrapped(i, 1);
            PathNode c1 = getWrapped(i, i == 0 ? 0 : -1);
            PathNode c2 = getWrapped(i, i == 0 ? 1 : 2);

            LinkedList<InterpolationData> splinePoints = new LinkedList<>();

            for(float j = 0; j <= 1; j += ClientConfigManager.CLIENT_CONFIG.genSettings.curveDetail.detail) {
                Vec3d spline, rotation;
                float zoom;
                if(nodes.size() == 2) {
                    spline = new Vec3d(
                            InterpolationMath.interpolateLinear(start.getPosition().getX(), end.getPosition().getX(), j),
                            InterpolationMath.interpolateLinear(start.getPosition().getY(), end.getPosition().getY(), j),
                            InterpolationMath.interpolateLinear(start.getPosition().getZ(), end.getPosition().getZ(), j));

                    rotation = new Vec3d(
                            InterpolationMath.interpolateLinear(start.getPitch(), end.getPitch(), j),
                            InterpolationMath.interpolateLinear(start.getYaw(), end.getYaw(), j),
                            InterpolationMath.interpolateLinear(start.getRoll(), end.getRoll(), j));

                    zoom = (float)InterpolationMath.interpolateLinear(start.getZoom(), end.getZoom(), j);

                } else {
                    spline = new Vec3d(
                            InterpolationMath.interpolateHermite(new double[]{c1.getPosition().getX(), start.getPosition().getX(), end.getPosition().getX(), c2.getPosition().getX()}, j, 0, 1),
                            InterpolationMath.interpolateHermite(new double[]{c1.getPosition().getY(), start.getPosition().getY(), end.getPosition().getY(), c2.getPosition().getY()}, j, 0, 1),
                            InterpolationMath.interpolateHermite(new double[]{c1.getPosition().getZ(), start.getPosition().getZ(), end.getPosition().getZ(), c2.getPosition().getZ()}, j, 0, 1));

                    rotation = new Vec3d(
                            InterpolationMath.interpolateHermite(new double[]{c1.getPitch(), start.getPitch(), end.getPitch(), c2.getPitch()}, j, 0, 1),
                            InterpolationMath.interpolateHermite(new double[]{c1.getYaw(), start.getYaw(), end.getYaw(), c2.getYaw()}, j, 0, 1),
                            InterpolationMath.interpolateHermite(new double[]{c1.getRoll(), start.getRoll(), end.getRoll(), c2.getRoll()}, j, 0, 1));

                    zoom = (float)InterpolationMath.interpolateHermite(new double[]{c1.getZoom(), start.getZoom(), end.getZoom(), c2.getZoom()}, j, 0, 1);
                }

                splinePoints.add(new InterpolationData(spline, rotation, zoom));
            }

            PathNode node = nodes.get(i + 1);
            splinePoints.add(new InterpolationData(end.getPosition(), new Vec3d(node.getPitch(), node.getYaw(), node.getRoll()), node.getZoom()));
            interpolation.put(nodes.get(i), splinePoints);
        }
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

    private JsonElement toJson(String filename) throws PathSerializationException {
        Optional<JsonElement> json = JsonOps.INSTANCE.withEncoder(CODEC).apply(this).resultOrPartial(System.out::println);
        if(json.isPresent()) {
            JsonObject obj = json.get().getAsJsonObject();
            obj.addProperty("Id", Shutter.id(filename).toString());
            return obj;
        } else
            throw new PathSerializationException("");
    }

    private PathNode getWrapped(int cur, int offset) {
        return nodes.get((cur + offset + nodes.size()) % nodes.size());
    }

    public static final Codec<CameraPath> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                SerializationUtils.CODEC_IDENTIFIER.fieldOf("Id").forGetter(CameraPath::getId),
                PathNode.CODEC.listOf().fieldOf("Nodes").forGetter(CameraPath::getNodes))
            .apply(i, (id, nodes) -> new CameraPath(id, new LinkedList<>(nodes))));
}
