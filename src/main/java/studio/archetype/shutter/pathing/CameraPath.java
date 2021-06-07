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
import studio.archetype.shutter.client.util.InterpolationMath;
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

        boolean wasWrapped = false;
        float wrapEnd = 0;

        for (int i = 0; i < nodes.size() - (looped ? 0 : 1); i++) {
            PathNode start = getWrapped(i, 0);
            PathNode end = getWrapped(i, 1);
            PathNode c1 = getWrapped(i, -1);
            PathNode c2 = getWrapped(i, 2);
            if(!looped && i == 0) {
                c1 = getWrapped(i, 0);
                c2 = getWrapped(i, 1);
            }

            LinkedList<InterpolationData> splinePoints = new LinkedList<>();

            float startYaw = start.getYaw();
            float endYaw = end.getYaw();

            if(wasWrapped) {
                startYaw = wrapEnd;
                wasWrapped = false;
            }

            while(Math.abs(startYaw - endYaw) > 180) {
                if(endYaw > startYaw) {
                    float newEnd = startYaw + 360;
                    startYaw  = endYaw;
                    endYaw = newEnd;
                } else if(endYaw < startYaw) {
                    endYaw += 360;
                }

                wasWrapped = true;
                wrapEnd = endYaw;
            }

            for(float ii = 0; ii <= 1; ii += ClientConfigManager.CLIENT_CONFIG.genSettings.curveDetail.detail) {
                Vec3d spline, rotation;
                float zoom;
                if(nodes.size() == 2) {
                    spline = new Vec3d(
                            InterpolationMath.interpolateLinear(start.getPosition().getX(), end.getPosition().getX(), ii),
                            InterpolationMath.interpolateLinear(start.getPosition().getY(), end.getPosition().getY(), ii),
                            InterpolationMath.interpolateLinear(start.getPosition().getZ(), end.getPosition().getZ(), ii));

                    rotation = new Vec3d(
                            InterpolationMath.interpolateLinear(start.getPitch(), end.getPitch(), ii),
                            InterpolationMath.interpolateLinear(startYaw, endYaw, ii),
                            InterpolationMath.interpolateLinear(start.getRoll(), end.getRoll(), ii));

                    zoom = (float)InterpolationMath.interpolateLinear(start.getZoom(), end.getZoom(), ii);

                } else {
                    spline = new Vec3d(
                            InterpolationMath.interpolateHermite(new double[]{c1.getPosition().getX(), start.getPosition().getX(), end.getPosition().getX(), c2.getPosition().getX()}, ii, 0, 1),
                            InterpolationMath.interpolateHermite(new double[]{c1.getPosition().getY(), start.getPosition().getY(), end.getPosition().getY(), c2.getPosition().getY()}, ii, 0, 1),
                            InterpolationMath.interpolateHermite(new double[]{c1.getPosition().getZ(), start.getPosition().getZ(), end.getPosition().getZ(), c2.getPosition().getZ()}, ii, 0, 1));

                    rotation = new Vec3d(
                            InterpolationMath.interpolateHermite(new double[]{c1.getPitch(), start.getPitch(), end.getPitch(), c2.getPitch()}, ii, 0, 1),
                            InterpolationMath.interpolateHermite(new double[]{c1.getYaw(), startYaw, endYaw, c2.getYaw()}, ii, 0, 1),
                            InterpolationMath.interpolateHermite(new double[]{c1.getRoll(), start.getRoll(), end.getRoll(), c2.getRoll()}, ii, 0, 1));

                    zoom = (float)InterpolationMath.interpolateHermite(new double[]{c1.getZoom(), start.getZoom(), end.getZoom(), c2.getZoom()}, ii, 0, 1);
                }
                splinePoints.add(new InterpolationData(spline, rotation, zoom));
            }

            splinePoints.add(new InterpolationData(end.getPosition(), new Vec3d(end.getPitch(), endYaw, end.getRoll()), end.getZoom()));

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
