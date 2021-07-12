package studio.archetype.shutter.pathing.interpolation;

import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.pathing.InterpolationData;
import studio.archetype.shutter.pathing.PathNode;

import java.util.LinkedList;

public class LinearInterpolator extends Interpolator {

    public LinearInterpolator(LinkedList<PathNode> nodes, boolean looped) {
        super(nodes, looped);
    }

    @Override
    public InterpolationData interpolate(int segment, float step) {
        PathNode start = getWrapped(segment, 0);
        PathNode end = getWrapped(segment, 1);

        double x, y, z;
        x = interpolateLinear(start.getPosition().getX(), end.getPosition().getX(), step);
        y = interpolateLinear(start.getPosition().getY(), end.getPosition().getY(), step);
        z = interpolateLinear(start.getPosition().getZ(), end.getPosition().getZ(), step);
        Vec3d position = new Vec3d(x, y, z);

        double pitch, yaw, roll;
        pitch = interpolateLinear(start.getPitch(), end.getPitch(), step);
        yaw = interpolateLinear(start.getYaw(), end.getYaw(), step);
        roll = interpolateLinear(start.getRoll(), end.getRoll(), step);
        Vec3d rotation = new Vec3d(pitch, yaw, roll);

        float zoom = (float) interpolateLinear(start.getZoom(), end.getZoom(), step);

        return new InterpolationData(position, rotation, zoom);
    }

    private double interpolateLinear(double start, double end, double step) {
        return (start * (1 - step) + end * step);
    }
}
