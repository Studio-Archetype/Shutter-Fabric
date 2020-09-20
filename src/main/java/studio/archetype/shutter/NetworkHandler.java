package studio.archetype.shutter;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.PathNode;

public final class NetworkHandler {

    public static final Identifier PACKET_KEY_NODE_CREATE = Shutter.id("node_create");
    public static final Identifier PACKET_KEY_PATH_SHOW = Shutter.id("path_show");

    public static void register() {
        ServerSidePacketRegistry.INSTANCE.register(PACKET_KEY_NODE_CREATE, (ctx, data) -> {
            Identifier id = CameraPathManager.DEFAULT_PATH;
            if(data.readBoolean())
                id = new Identifier(data.readString());
            PathNode node = PathNode.deserialize(data);
            Shutter.INSTANCE.getPathManager(ctx.getPlayer().getEntityWorld()).addNode(id, node);
        });

        ServerSidePacketRegistry.INSTANCE.register(PACKET_KEY_PATH_SHOW, (ctx, data) -> {
            Identifier id = new Identifier(data.readString());
            PlayerEntity player = ctx.getPlayer();
            Shutter.INSTANCE.getPathManager(ctx.getPlayer().getEntityWorld()).togglePathVisualization(player, id);
        });
    }
}
