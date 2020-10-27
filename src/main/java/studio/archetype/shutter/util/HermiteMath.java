package studio.archetype.shutter.util;

public final class HermiteMath {

    public static double interpolate(double[] points, double mu, double tension, double bias) {
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
}
