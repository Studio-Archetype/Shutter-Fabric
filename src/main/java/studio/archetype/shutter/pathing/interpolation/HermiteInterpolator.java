package studio.archetype.shutter.pathing.interpolation;

import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.pathing.InterpolationData;
import studio.archetype.shutter.pathing.PathNode;

import java.util.LinkedList;

public class HermiteInterpolator extends Interpolator {

    public HermiteInterpolator(LinkedList<PathNode> nodes, boolean looped) {
        super(nodes, looped);
    }

    @Override
    public InterpolationData interpolate(int startIndex, float step) {
        PathNode start = getWrapped(startIndex, 0);
        PathNode end = getWrapped(startIndex, 1);
        PathNode c1 = getWrapped(startIndex, -1);
        PathNode c2 = getWrapped(startIndex, 2);

        double x, y, z;
        x = interpolateHermite(start.getPosition().getX(), end.getPosition().getX(), c1.getPosition().getX(), c2.getPosition().getX(), step, 0, 1);
        y = interpolateHermite(start.getPosition().getY(), end.getPosition().getY(), c1.getPosition().getY(), c2.getPosition().getY(), step, 0, 1);
        z = interpolateHermite(start.getPosition().getZ(), end.getPosition().getZ(), c1.getPosition().getZ(), c2.getPosition().getZ(), step, 0, 1);
        Vec3d position = new Vec3d(x, y, z);

        double pitch, yaw, roll;
        pitch = interpolateHermite(start.getPitch(), end.getPitch(), c1.getPitch(), c2.getPitch(), step, 0, 1);
        yaw = interpolateHermite(start.getYaw(), end.getYaw(), c1.getYaw(), c2.getYaw(), step, 0, 1);
        roll = interpolateHermite(start.getRoll(), end.getRoll(), c1.getRoll(), c2.getRoll(), step, 0, 1);
        Vec3d rotation = new Vec3d(pitch, yaw, roll);

        float zoom = (float) interpolateHermite(start.getZoom(), end.getZoom(), c1.getZoom(), c2.getZoom(), step, 0, 1);

        return new InterpolationData(position, rotation, zoom);
    }

    private double interpolateHermite(double start, double end, double c1, double c2, double step, double tension, double bias) {
        double m0, m1, mu2, mu3;
        double a0, a1, a2, a3;

        mu2 = step * step;
        mu3 = mu2 * step;

        m0 = (start - c1) * (1 + bias) * (1 - tension) / 2;
        m0 += (end - start) * (1 - bias) * (1 - tension) / 2;
        m1 = (end - start) * (1 + bias) * (1 - tension) / 2;
        m1 += (c2 - end) * (1 - bias) * (1 - tension) / 2;

        a0 = 2 * mu3 - 3 * mu2 + 1;
        a1 = mu3 - 2 * mu2 + step;
        a2 = mu3 - mu2;
        a3 = -2 * mu3 + 3 * mu2;

        return (a0 * start + a1 * m0 + a2 * m1 + a3 * end);
    }
}
