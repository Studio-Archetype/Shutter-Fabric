package studio.archetype.shutter.client;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.NetworkHandler;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.networking.PacketS2CPathVisualization;
import studio.archetype.shutter.networking.PacketS2CSpawnNodeVisualization;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.PathNode;

import java.util.UUID;

public class ClientNetworkHandler {

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
        ClientSidePacketRegistry.INSTANCE.register(PacketS2CSpawnNodeVisualization.ID, PacketS2CSpawnNodeVisualization::onReceive);
        ClientSidePacketRegistry.INSTANCE.register(PacketS2CPathVisualization.ID, PacketS2CPathVisualization::onReceive);
    }
}
