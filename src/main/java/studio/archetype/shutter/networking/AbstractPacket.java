package studio.archetype.shutter.networking;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public abstract class AbstractPacket {

    protected static PacketByteBuf getBuffer() {
        return new PacketByteBuf(Unpooled.buffer());
    }

    protected static void writeVec3d(PacketByteBuf buf, Vec3d val) {
        buf.writeDouble(val.getX()); buf.writeDouble(val.getY()); buf.writeDouble(val.getZ());
    }

    protected static Vec3d readVec3d(PacketByteBuf buf) {
        return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }
}
