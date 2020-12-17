package studio.archetype.shutter.pathing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.util.InterpolationMath;

import java.util.*;

public class CameraPath {

    public final Identifier id;
    private final LinkedList<PathNode> nodes = new LinkedList<>();

    private final Map<PathNode, LinkedList<Vec3d>> interpolation = new HashMap<>();
    boolean needsInterpolationRebuilt = true;

    public CameraPath(Identifier id) {
        this.id = id;
    }

    public CameraPath(CompoundTag tag) {
        this.id = readFromNbt(tag);
    }

    public void addNode(PathNode node) {
        this.nodes.add(node);
        needsInterpolationRebuilt = true;
    }

    public LinkedList<PathNode> getNodes() {
        return nodes;
    }

    public Map<PathNode, LinkedList<Vec3d>> getInterpolatedData() {
        if(needsInterpolationRebuilt)
            calculatePath();
        return interpolation;
    }

    public void clear() {
        nodes.clear();
        interpolation.clear();
        needsInterpolationRebuilt = false;
    }

    public Identifier readFromNbt(CompoundTag compoundTag) {
        Identifier id = new Identifier(compoundTag.getString("id"));
        this.nodes.clear();
        compoundTag.getList("nodes", 10).forEach(t -> {
            CompoundTag tag = (CompoundTag)t;
            this.nodes.add(new PathNode(tag));
        });
        return id;
    }

    public void writeToNbt(CompoundTag tag) {
        CompoundTag path = new CompoundTag();
        path.putString("id", this.id.toString());
        ListTag nodes = new ListTag();
        this.nodes.forEach(n -> {
            CompoundTag node = new CompoundTag();
            n.serialize(node);
            nodes.add(node);
        });
        path.put("nodes", nodes);
    }

    public void calculatePath() {
        interpolation.clear();
        for (int i = 0; i < nodes.size() - 1; i++) {
            Vec3d p1 = getWrapped(i, -1).getPosition();
            Vec3d p2 = getWrapped(i, 0).getPosition();
            Vec3d p3 = getWrapped(i, 1).getPosition();
            Vec3d p4 = getWrapped(i, 2).getPosition();

            LinkedList<Vec3d> splinePoints = new LinkedList<>();

            for(float j = 0; j <= 1; j += ClientConfigManager.CLIENT_CONFIG.curveDetail) {
                Vec3d spline;

                if(nodes.size() == 2)
                    spline = new Vec3d(
                            InterpolationMath.interpolateLinear(p2.getX(), p3.getX(), j),
                            InterpolationMath.interpolateLinear(p2.getY(), p3.getY(), j),
                            InterpolationMath.interpolateLinear(p2.getZ(), p3.getZ(), j)
                    );
                else
                    spline = new Vec3d(
                            InterpolationMath.interpolateHermite(new double[] {p1.getX(), p2.getX(), p3.getX(), p4.getX()}, j, 0, 1),
                            InterpolationMath.interpolateHermite(new double[] {p1.getY(), p2.getY(), p3.getY(), p4.getY()}, j, 0, 1),
                            InterpolationMath.interpolateHermite(new double[] {p1.getZ(), p2.getZ(), p3.getZ(), p4.getZ()}, j, 0, 1)
                    );

                splinePoints.add(spline);
            }

            splinePoints.add(p3);
            interpolation.put(nodes.get(i), splinePoints);
        }
        needsInterpolationRebuilt = false;
    }

    private PathNode getWrapped(int cur, int offset) {
        return nodes.get((cur + offset + nodes.size()) % nodes.size());
    }
}
