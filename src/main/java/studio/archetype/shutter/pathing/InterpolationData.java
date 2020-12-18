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

    public Vec3d getPosition() {
        return position;
    }

    public Vec3d getRotation() {
        return rotation;
    }

    public float getZoom() {
        return zoom;
    }
}