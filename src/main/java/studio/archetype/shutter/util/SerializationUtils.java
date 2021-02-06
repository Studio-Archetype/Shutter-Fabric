package studio.archetype.shutter.util;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public final class SerializationUtils {

    public static final Codec<Identifier> CODEC_IDENTIFIER = Codec.STRING
            .fieldOf("Id")
            .codec()
            .xmap(Identifier::new, Identifier::toString);

    public static List<Double> vec3dToList(Vec3d vec) {
        return Lists.newArrayList(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vec3d listToVec3d(List<Double> list) {
        return new Vec3d(list.get(0), list.get(1), list.get(2));
    }

    public static List<Float> vector3fToList(Vector3f vec) {
        return Lists.newArrayList(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vector3f listToVector3f(List<Float> list) {
        return new Vector3f(list.get(0), list.get(1), list.get(2));
    }
}
