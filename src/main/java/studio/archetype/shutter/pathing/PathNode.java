package studio.archetype.shutter.pathing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import studio.archetype.shutter.util.SerializationUtils;

public class PathNode {

    private final Vec3d position;
    private final float pitch, yaw, roll;
    private final float zoom;

    public PathNode(Vec3d pos, float pitch, float yaw, float roll, float zoom) {
        this.position = pos;
        this.pitch = pitch;
        float yawAdj = yaw % 360;
        this.yaw = yawAdj < 0 ? 360 + yawAdj : yawAdj;
        this.roll = roll;
        this.zoom = zoom;
    }

    private PathNode(Vec3d pos, Vec3f rot, float zoom) {
        this(pos, rot.getX(), rot.getY(), rot.getZ(), zoom);
    }

    public Vec3d getPosition() {
        return position;
    }

    public Vec3f getRotation() { return new Vec3f(pitch, yaw, roll); }

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

    public static final Codec<PathNode> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                Codec.DOUBLE.listOf().fieldOf("Position").forGetter((PathNode o) -> SerializationUtils.vec3dToList(o.getPosition())),
                Codec.FLOAT.listOf().fieldOf("Rotation").forGetter((PathNode o) -> SerializationUtils.vector3fToList(o.getRotation())),
                Codec.FLOAT.fieldOf("Zoom").forGetter(PathNode::getZoom))
            .apply(i, (pos, rot, zoom) -> new PathNode(SerializationUtils.listToVec3d(pos), SerializationUtils.listToVector3f(rot), zoom)));
}
