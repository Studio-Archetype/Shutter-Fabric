package studio.archetype.shutter.client.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;
import studio.archetype.shutter.client.extensions.CameraExt;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.PathNode;

import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.argument;
import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.literal;

public final class PathNodeCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(
                literal("s")
                        .requires(src -> src.hasPermissionLevel(4))
                        .then(literal("add")
                                .executes(PathNodeCommand::addNode))
                        .then(literal("remove")
                                .then(argument("index", IntegerArgumentType.integer())
                                    .executes(ctx -> removeNode(ctx, IntegerArgumentType.getInteger(ctx, "index")))))
                        .then(literal("set")
                                .then(argument("index", IntegerArgumentType.integer())
                                        .executes(ctx -> setNode(ctx, IntegerArgumentType.getInteger(ctx, "index")))))
                        .then(literal("goto")
                                .then(argument("index", IntegerArgumentType.integer())
                                        .executes(ctx -> gotoNode(ctx, IntegerArgumentType.getInteger(ctx, "index"))))));

        dispatcher.register(
                literal("shutter")
                        .redirect(node));
    }

    private static int addNode(CommandContext<FabricClientCommandSource> ctx) {
        MinecraftClient c = ctx.getSource().getClient();
        Camera cam = c.gameRenderer.getCamera();
        PathNode node = new PathNode(cam.getPos(), cam.getPitch(), cam.getYaw(), ((CameraExt)cam).getRoll(1.0F), (float)ShutterClient.INSTANCE.getZoom());
        ShutterClient.INSTANCE.getPathManager(c.world).addNode(CameraPathManager.DEFAULT_PATH, node);
        ctx.getSource().sendFeedback(Text.of("Created Node #" + (ShutterClient.INSTANCE.getPathManager(c.world).getPath(CameraPathManager.DEFAULT_PATH).getNodes().size() - 1) + "."));
        return 1;
    }

    private static int removeNode(CommandContext<FabricClientCommandSource> ctx, int index) {
        MinecraftClient c = ctx.getSource().getClient();
        CameraPath path = ShutterClient.INSTANCE.getPathManager(c.world).getPath(CameraPathManager.DEFAULT_PATH);
        try {
            path.removeNode(index);
            ctx.getSource().sendFeedback(Text.of("Removed Node #" + index + " from path."));
            return 1;
        } catch(IndexOutOfBoundsException e) {
            ctx.getSource().sendError(Text.of("Unable to remove Node #" + index + ": Out of bounds!"));
            return 0;
        }
    }

    private static int setNode(CommandContext<FabricClientCommandSource> ctx, int index) {
        MinecraftClient c = ctx.getSource().getClient();
        CameraPath path = ShutterClient.INSTANCE.getPathManager(c.world).getPath(CameraPathManager.DEFAULT_PATH);
        try {
            Camera cam = c.gameRenderer.getCamera();
            PathNode node = new PathNode(cam.getPos(), cam.getPitch(), cam.getYaw(), ((CameraExt)cam).getRoll(1.0F), (float)ShutterClient.INSTANCE.getZoom());

            path.setNode(node, index);
            ctx.getSource().sendFeedback(Text.of("Replaced Node #" + index + "."));
            return 1;
        } catch(IndexOutOfBoundsException e) {
            ctx.getSource().sendError(Text.of("Unable to replace Node #" + index + ": Out of bounds!"));
            return 0;
        }
    }

    private static int gotoNode(CommandContext<FabricClientCommandSource> ctx, int index) {
        MinecraftClient c = ctx.getSource().getClient();
        ClientPlayerEntity p = c.player;
        CameraPath path = ShutterClient.INSTANCE.getPathManager(c.world).getPath(CameraPathManager.DEFAULT_PATH);
        try {
            PathNode node = path.getNodes().get(index);
            Vec3d position = node.getPosition();
            p.setPos(position.getX(), position.getY() - (16 * 1.3), position.getZ());
            p.pitch = node.getPitch(); p.yaw = node.getYaw(); ((CameraExt)c.gameRenderer.getCamera()).setRoll(node.getRoll());
            ShutterClient.INSTANCE.setZoom(node.getZoom());
            ctx.getSource().sendFeedback(Text.of("Going to Node #" + index + "."));
            return 1;
        } catch(IndexOutOfBoundsException e) {
            ctx.getSource().sendError(Text.of("Unable to go to Node #" + index + ": Out of bounds!"));
            return 0;
        }
    }
}
