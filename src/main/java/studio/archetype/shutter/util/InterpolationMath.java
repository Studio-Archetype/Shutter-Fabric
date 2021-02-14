package studio.archetype.shutter.util;

import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.pathing.PathNode;

public final class InterpolationMath {

    public static double interpolateLinear(double start, double end, double mu) {
        return (start * (1 - mu) + end * mu);
    }

    public static double interpolateHermite(double[] points, double mu, double tension, double bias) {
        double m0, m1, mu2, mu3;
        double a0, a1, a2, a3;

        mu2 = mu * mu;
        mu3 = mu2 * mu;

        m0 = (points[1] - points[0]) * (1 + bias) * (1 - tension) / 2;
        m0 += (points[2] - points[1]) * (1 - bias) * (1 - tension) / 2;
        m1 = (points[2] - points[1]) * (1 + bias) * (1 - tension) / 2;
        m1 += (points[3] - points[2]) * (1 - bias) * (1 - tension) / 2;

        a0 = 2 * mu3 - 3 * mu2 + 1;
        a1 = mu3 - 2 * mu2 + mu;
        a2 = mu3 - mu2;
        a3 = -2 * mu3 + 3 * mu2;

        return (a0 * points[1] + a1 * m0 + a2 * m1 + a3 * points[2]);
    }

    public static PathNode getYawDifferenceNode(PathNode last, PathNode first) {
        Vec3d position = first.getPosition();
        float zoom = first.getZoom();

        int previousRotations = (int)last.getYaw() / 360;
        float newYaw =  (previousRotations * 360) + (first.getYaw() % 360);

        if(((last.getYaw() > 0 && first.getYaw() < 0) || (last.getYaw() < 0 && first.getYaw() > 0)) && previousRotations == 0)
            newYaw = newYaw * -1;

        System.out.printf("Old: %.2f | New: %.2f | Determined: %.2f%n", last.getYaw(), first.getYaw(), newYaw);

        return new PathNode(position, first.getPitch(), newYaw, first.getRoll(), zoom);
    }
}
