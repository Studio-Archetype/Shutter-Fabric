package studio.archetype.shutter.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

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

        for (Vec3d point : points)
            consumer.vertex(model, (float) point.x, (float) point.y, (float) point.z)
                    .color((float)colour.x, (float)colour.y, (float)colour.z, 1f)
                    .next();
    }

    public static void renderCube(Vec3d pos, float radius, Vec3d colour, VertexConsumer consumer, Matrix4f model) {
        Stream.of(Direction.values()).forEach(dir -> DrawUtils.quadOffsetAxis(pos, radius, dir, colour, consumer, model));
    }
}
