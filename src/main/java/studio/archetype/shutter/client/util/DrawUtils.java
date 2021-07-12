package studio.archetype.shutter.client.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;

import java.util.stream.Stream;

public class DrawUtils {

    public static void quadOffsetAxis(Vec3d pos, float radius, Direction dir, Vec3d colour, VertexConsumer consumer, Matrix4f model) {
        Vec3i dirVector = dir.getVector();
        Vec3d offset = pos.add(new Vec3d(dirVector.getX() * radius, dirVector.getY() * radius, dirVector.getZ() * radius));
        Vec3d[] points = new Vec3d[4];
        switch(dir.getAxis()) {
            case X:
                points[0] = offset.add(new Vec3d(0, radius, radius));
                points[1] = offset.add(new Vec3d(0, -radius, radius));
                points[2] = offset.add(new Vec3d(0, -radius, -radius));
                points[3] = offset.add(new Vec3d(0, radius, -radius));
                break;
            case Y:
                points[0] = offset.add(new Vec3d(radius, 0, radius));
                points[1] = offset.add(new Vec3d(radius, 0, -radius));
                points[2] = offset.add(new Vec3d(-radius, 0, -radius));
                points[3] = offset.add(new Vec3d(-radius, 0, radius));
                break;
            case Z:
                points[0] = offset.add(new Vec3d(radius, radius, 0));
                points[1] = offset.add(new Vec3d(-radius, radius, 0));
                points[2] = offset.add(new Vec3d(-radius, -radius, 0));
                points[3] = offset.add(new Vec3d(radius, -radius, 0));
                break;
        }

        if(dir.getDirection().offset() < 0) {
            Vec3d t = points[1];
            points[1] = points[3];
            points[3] = t;
        }

        for(Vec3d point : points)
            consumer.vertex(model, (float) point.x, (float) point.y, (float) point.z)
                    .color((float) colour.x, (float) colour.y, (float) colour.z, 1f)
                    .next();
    }

    public static void renderCube(Vec3d pos, float radius, Vec3d colour, VertexConsumer consumer, MatrixStack.Entry stack) {
        Stream.of(Direction.values()).forEach(dir -> DrawUtils.quadOffsetAxis(pos, radius, dir, colour, consumer, stack.getModel()));
    }

    public static void renderLine(Vec3d pos, Vec3d pos2, Vec3d colour, VertexConsumer consumer, MatrixStack.Entry stack) {
        consumer.vertex(stack.getModel(), (float) pos.getX(), (float) pos.getY(), (float) pos.getZ())
                .color((float) colour.x, (float) colour.y, (float) colour.z, 1.0F)
                .next();
        consumer.vertex(stack.getModel(), (float) pos2.getX(), (float) pos2.getY(), (float) pos2.getZ())
                .color((float) colour.x, (float) colour.y, (float) colour.z, 1.0F)
                .next();
    }

    public static Vec3d getOffsetPoint(Vec3d pos, float pitch, float yaw, double distance) {
        Vec3d dir = getRotationVector(pitch, yaw);
        return pos.add(new Vec3d(dir.getX() * distance, dir.getY() * distance, dir.getZ() * distance));
    }

    public static Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }
}
