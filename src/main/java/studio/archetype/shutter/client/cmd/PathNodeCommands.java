package studio.archetype.shutter.client.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.extensions.CameraExt;
import studio.archetype.shutter.client.ui.Messaging;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.PathNode;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public final class PathNodeCommands {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(
                literal("s")
                        .requires(src -> src.hasPermissionLevel(4))
                        .then(literal("add")
                                .executes(PathNodeCommands::addNode))
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
        ShutterClient shutter = ShutterClient.INSTANCE;
        PathNode node = new PathNode(cam.getPos(), cam.getPitch(), cam.getYaw(), ((CameraExt)cam).getRoll(1.0F), (float)shutter.getZoom());
        shutter.getPathManager(c.world).addNode(node);
        return 1;
    }

    private static int removeNode(CommandContext<FabricClientCommandSource> ctx, int index) {
        MinecraftClient c = ctx.getSource().getClient();
        CameraPath path = ShutterClient.INSTANCE.getPathManager(c.world).getCurrentPath();
        try {
            path.removeNode(index);
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.success"),
                    new TranslatableText("msg.shutter.ok.remove_node", index),
                    Messaging.MessageType.POSITIVE);
            return 1;
        } catch(IndexOutOfBoundsException e) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.remove_node", index),
                    Messaging.MessageType.NEGATIVE);
            return 0;
        }
    }

    private static int setNode(CommandContext<FabricClientCommandSource> ctx, int index) {
        MinecraftClient c = ctx.getSource().getClient();
        CameraPath path = ShutterClient.INSTANCE.getPathManager(c.world).getCurrentPath();
        try {
            Camera cam = c.gameRenderer.getCamera();
            PathNode node = new PathNode(cam.getPos(), cam.getPitch(), cam.getYaw(), ((CameraExt)cam).getRoll(1.0F), (float)ShutterClient.INSTANCE.getZoom());
            path.setNode(node, index);
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.success"),
                    new TranslatableText("msg.shutter.ok.set_node", index),
                    new LiteralText(" x").formatted(Formatting.DARK_RED)
                            .append(new LiteralText(String.format("%.3f", node.getPosition().x)).formatted(Formatting.RED, Formatting.UNDERLINE))
                            .append(new LiteralText(" y").formatted(Formatting.DARK_GREEN))
                            .append(new LiteralText(String.format("%.3f", node.getPosition().y)).formatted(Formatting.GREEN, Formatting.UNDERLINE))
                            .append(new LiteralText(" z").formatted(Formatting.DARK_BLUE))
                            .append(new LiteralText(String.format("%.3f", node.getPosition().z)).formatted(Formatting.BLUE, Formatting.UNDERLINE)),
                    Messaging.MessageType.POSITIVE);
            return 1;
        } catch(IndexOutOfBoundsException e) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.set_node", index),
                    Messaging.MessageType.NEGATIVE);
            return 0;
        }
    }

    private static int gotoNode(CommandContext<FabricClientCommandSource> ctx, int index) {
        MinecraftClient c = ctx.getSource().getClient();
        CameraPath path = ShutterClient.INSTANCE.getPathManager(c.world).getCurrentPath();
        try {
            PathNode node = path.getNodes().get(index);
            Vec3d position = node.getPosition().add(0, -1.62, 0);
            ShutterClient.INSTANCE.getCommandFilter().teleportClient(position, node.getPitch(), node.getYaw());
            ((CameraExt)c.gameRenderer.getCamera()).setRoll(node.getRoll());
            ShutterClient.INSTANCE.setZoom(node.getZoom());
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.success"),
                    new TranslatableText("msg.shutter.ok.go_to_node", index),
                    Messaging.MessageType.POSITIVE);
            return 1;
        } catch(IndexOutOfBoundsException e) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.go_to_node", index),
                    Messaging.MessageType.NEGATIVE);
            return 0;
        }
    }
}
