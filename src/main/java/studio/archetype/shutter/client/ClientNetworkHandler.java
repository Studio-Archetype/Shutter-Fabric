package studio.archetype.shutter.client;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.NetworkHandler;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.PathNode;

import java.util.UUID;

public final class ClientNetworkHandler {

    public static final Identifier PACKET_ENTITY_SPAWN_NODE = Shutter.id("spawn_node");

    public static void sendCreateNode(PathNode node, Identifier id) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        node.serialize(buf, id);
        ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkHandler.PACKET_KEY_NODE_CREATE, buf);
    }

    public static void sendShowPath(Identifier id) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        if(id == null)
            buf.writeString(CameraPathManager.DEFAULT_PATH.toString());
        else
            buf.writeString(id.toString());
        ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkHandler.PACKET_KEY_PATH_SHOW, buf);
    }

    public static void register() {
        ClientSidePacketRegistry.INSTANCE.register(PACKET_ENTITY_SPAWN_NODE, (ctx, data) -> {
            UUID uuid = data.readUuid();
            int entityId = data.readVarInt();
            double x = data.readDouble(); double y = data.readDouble(); double z = data.readDouble();
            float pitch = data.readFloat(); float yaw = data.readFloat(); float roll = data.readFloat();
            ctx.getTaskQueue().execute(() -> {
            });
        });
    }
}
