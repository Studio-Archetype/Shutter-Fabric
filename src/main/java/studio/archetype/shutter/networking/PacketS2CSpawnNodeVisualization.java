package studio.archetype.shutter.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.entities.CameraPointEntity;
import studio.archetype.shutter.entities.Entities;

import java.util.UUID;


public class PacketS2CSpawnNodeVisualization extends AbstractPacket {

    public static final Identifier ID = Shutter.id("node_create");

    public static Packet<?> sendPacket(CameraPointEntity entity) {
        PacketByteBuf buf = getBadChest();
        buf.writeUuid(entity.getUuid());
        buf.writeVarInt(entity.getEntityId());
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeFloat(entity.pitch);
        buf.writeFloat(entity.yaw);
        buf.writeFloat(entity.roll);
        return ServerSidePacketRegistry.INSTANCE.toPacket(ID, buf);
    }

    @Environment(EnvType.CLIENT)
    public static void onReceive(PacketContext ctx, PacketByteBuf byteBuf) {
        UUID entityUUID = byteBuf.readUuid();
        int entityID = byteBuf.readVarInt();
        double x = byteBuf.readDouble();
        double y = byteBuf.readDouble();
        double z = byteBuf.readDouble();
        float pitch = (byteBuf.readFloat());
        float yaw = (byteBuf.readFloat());
        float roll = (byteBuf.readFloat());
        ctx.getTaskQueue().execute(() -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            CameraPointEntity entity = Entities.ENTITY_CAMERA_POINT.create(world);
            if(entity != null) {
                entity.updatePosition(x, y, z);
                entity.updateTrackedPosition(x, y, z);
                entity.pitch = pitch;
                entity.yaw = yaw;
                entity.roll = roll;
                entity.setEntityId(entityID);
                entity.setUuid(entityUUID);
                world.addEntity(entityID, entity);
            }
        });
    }
}
