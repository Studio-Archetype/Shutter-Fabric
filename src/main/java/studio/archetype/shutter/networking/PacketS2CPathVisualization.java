package studio.archetype.shutter.networking;

import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.pathing.PathNode;

import java.util.LinkedList;

public class PacketS2CPathVisualization extends AbstractPacket {

    public static final Identifier ID = Shutter.id("path_show");

    public static Packet<?> sendPacket(LinkedList<PathNode> pathNodes) {
        PacketByteBuf buf = getBuffer();
        if(pathNodes == null) {
            buf.writeVarInt(0);
        } else {
            buf.writeVarInt(pathNodes.size());
            pathNodes.forEach(n -> {
                writeVec3d(buf, n.getPosition());
            });
        }
        return ServerSidePacketRegistry.INSTANCE.toPacket(ID, buf);
    }

    public static void onReceive(PacketContext ctx, PacketByteBuf buf) {
        LinkedList<Vec3d> points = new LinkedList<>();
        int size = buf.readVarInt();
        if(size < 2) {
            ctx.getTaskQueue().execute(ShutterClient.INSTANCE.getPathManager()::resetPoints);
            return;
        }
        for (int i = 0; i < size; i++)
            points.add(readVec3d(buf));

        ctx.getTaskQueue().execute(() -> {
            ShutterClient.INSTANCE.getPathManager().setPoints(points);
        });
    }
}
