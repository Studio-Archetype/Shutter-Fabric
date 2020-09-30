package studio.archetype.shutter.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.util.HermiteMath;

import java.util.LinkedList;

public class ClientPathManager {

    private static final float PRECISION = 0.1F;

    private LinkedList<Vec3d> keyframes = new LinkedList<>();

    public void resetPoints() {
        keyframes.clear();
    }

    public void setPoints(LinkedList<Vec3d> points) {
        this.keyframes = points;
    }

    public void render(MatrixStack stack, VertexConsumerProvider.Immediate consume, Vec3d camera) {
        if(keyframes.isEmpty())
            return;

        stack.push();
        stack.translate(-camera.getX(), -camera.getY(), -camera.getZ());

        for (int i = 0; i < keyframes.size() - 1; i++) {
            Vec3d p1 = keyframes.get(i);
            Vec3d p2 = keyframes.get(i + 1);
            Vec3d p3 = getWrapped(i, 2);
            Vec3d p4 = getWrapped(i, 3);

            Vec3d previous = p1;

            for(float j = 0; j <= 1; j += PRECISION) {
                double x = HermiteMath.interpolate(new double[] {p1.getX(), p2.getX(), p3.getX(), p4.getX()}, j, 1, 1);
                double y = HermiteMath.interpolate(new double[] {p1.getY(), p2.getY(), p3.getY(), p4.getY()}, j, 1, 1);
                double z = HermiteMath.interpolate(new double[] {p1.getZ(), p2.getZ(), p3.getZ(), p4.getZ()}, j, 1, 1);

                Matrix4f model = stack.peek().getModel();
                VertexConsumer vert = consume.getBuffer(RenderLayer.getLines());

                vert.vertex(model, (float)previous.getX(), (float)previous.getY(), (float)previous.getZ()).color(0, 1, 0, 1).next();
                vert.vertex(model, (float)x, (float)y, (float)z).color(0, 1, 0, 1).next();

                previous = new Vec3d(x, y,z);
            }
        }

        stack.pop();
    }

    private Vec3d getWrapped(int cur, int offset) {
        return keyframes.get((cur + offset) % keyframes.size());
    }
}
