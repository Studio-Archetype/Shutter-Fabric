package studio.archetype.shutter.pathing;

import net.minecraft.util.math.Vec3d;

public class InterpolationData {

    private final Vec3d position, rotation;
    private final float zoom;

    public InterpolationData(Vec3d position, Vec3d rotation, float zoom) {
        this.position = position;
        this.rotation = rotation;
        this.zoom = zoom;
    }

    public InterpolationData(PathNode node) {
        this(node.getPosition(), new Vec3d(node.getPitch(), node.getYaw(), node.getRoll()), node.getZoom());
    }

    public Vec3d getPosition() {
        return position;
    }

    public Vec3d getRotation() {
        return rotation;
    }

    public float getZoom() {
        return zoom;
    }

    @Override
    public String toString() {
        return String.format("[X=%.2f|Y=%.2f|Z=%.2f||P=%.2f|Y=%.2f|R=%.2f||Z=%.2f]",
                this.position.getX(), this.position.getY(), this.position.getZ(),
                this.getRotation().getX(), this.getRotation().getY(), this.getRotation().getZ(), this.getZoom());
    }
}