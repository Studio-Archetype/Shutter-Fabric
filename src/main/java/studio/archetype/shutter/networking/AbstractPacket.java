package studio.archetype.shutter.networking;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public abstract class AbstractPacket {

    protected static PacketByteBuf getBadChest() {
        return new PacketByteBuf(Unpooled.buffer());
    }
}
