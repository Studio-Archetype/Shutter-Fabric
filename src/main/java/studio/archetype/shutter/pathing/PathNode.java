package studio.archetype.shutter.pathing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3d;

public class PathNode {

    private Vec3d position;
    private float pitch, yaw, roll;
    private float zoom;

    public PathNode(CompoundTag tag) {
        deserialize(tag);
    }

    public PathNode(Vec3d pos, float pitch, float yaw, float roll, float zoom) {
        this.position = pos;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.zoom = zoom;
    }

    public void serialize(CompoundTag tag) {
        tag.putDouble("x", this.getPosition().getX()); tag.putDouble("y", this.getPosition().getY()); tag.putDouble("z", this.getPosition().getZ());
        tag.putFloat("pitch", this.getPitch());
        tag.putFloat("yaw", this.getYaw());
        tag.putFloat("roll", this.getRoll());
        tag.putFloat("zoom", this.getZoom());
    }

    private void deserialize(CompoundTag tag) {
        this.position = new Vec3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
        this.pitch = tag.getFloat("pitch");
        this.yaw = tag.getFloat("yaw");
        this.roll = tag.getFloat("roll");
        this.zoom = tag.getFloat("zoom");
    }

    public Vec3d getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public float getZoom() {
        return zoom;
    }
}
