package studio.archetype.shutter.pathing;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class PathNode {

    private final Vec3d position;
    private final float pitch, yaw, roll;
    private final float zoom;

    public PathNode(Vec3d pos, float pitch, float yaw, float roll, float zoom) {
        this.position = pos;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.zoom = zoom;
    }

    public void serialize(PacketByteBuf buf, Identifier path) {
        buf.writeBoolean(path != null);
        if(path != null)
            buf.writeString(path.toString());

        buf.writeDouble(position.getX());
        buf.writeDouble(position.getY());
        buf.writeDouble(position.getZ());

        buf.writeFloat(pitch);
        buf.writeFloat(yaw);
        buf.writeFloat(roll);

        buf.writeDouble(zoom);
    }

    public static PathNode deserialize(PacketByteBuf buf) {
        Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        float pitch = buf.readFloat();
        float yaw = buf.readFloat();
        float roll = buf.readFloat();
        float zoom = (float)buf.readDouble();

        return new PathNode(pos, pitch, yaw, roll, zoom);
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
