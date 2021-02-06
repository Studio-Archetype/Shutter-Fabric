package studio.archetype.shutter.client.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;
import studio.archetype.shutter.client.ui.Messaging;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.exceptions.PathTooSmallException;

import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.literal;

public final class PathVisualCommands {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(
                literal("s")
                        .requires(src -> src.hasPermissionLevel(4))
                        .then(literal("show")
                                .executes(PathVisualCommands::showPath))
                        .then(literal("hide")
                                .executes(PathVisualCommands::hidePath))
                        .then(literal("toggle")
                                .executes(PathVisualCommands::togglePath)));

        dispatcher.register(
                literal("shutter")
                        .redirect(node));
    }

    private static int showPath(CommandContext<FabricClientCommandSource> ctx) {
        ClientPlayerEntity p = ctx.getSource().getPlayer();
        CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(p.getEntityWorld());
        try {
            if(manager.isVisualizing())
                Messaging.sendMessage(
                        new TranslatableText("msg.shutter.headline.cmd.success"),
                        new TranslatableText("msg.shutter.error.showing_path"),
                        Messaging.MessageType.NEUTRAL);
            else {
                manager.togglePathVisualization();
                Messaging.sendMessage(
                        new TranslatableText("msg.shutter.headline.cmd.failed"),
                        new TranslatableText("msg.shutter.ok.showing_path"),
                        Messaging.MessageType.NEUTRAL);
            }

            return 1;
        } catch(PathTooSmallException e) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.not_enough_show"),
                    Messaging.MessageType.NEGATIVE);
            return 0;
        }
    }

    private static int hidePath(CommandContext<FabricClientCommandSource> ctx) {
        ClientPlayerEntity p = ctx.getSource().getPlayer();
        CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(p.getEntityWorld());
        try {
            if(manager.isVisualizing()) {
                manager.togglePathVisualization();
                Messaging.sendMessage(
                        new TranslatableText("msg.shutter.headline.cmd.success"),
                        new TranslatableText("msg.shutter.ok.hiding_path"),
                        Messaging.MessageType.NEUTRAL);
            } else
                Messaging.sendMessage(
                        new TranslatableText("msg.shutter.headline.cmd.failed"),
                        new TranslatableText("msg.shutter.error.hiding_path"),
                        Messaging.MessageType.NEUTRAL);
            return 1;
        } catch(PathTooSmallException ignored) { }

        return 0;
    }

    private static int togglePath(CommandContext<FabricClientCommandSource> ctx) {
        ClientPlayerEntity p = ctx.getSource().getPlayer();
        CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(p.getEntityWorld());
        try {
            if(manager.togglePathVisualization())
                Messaging.sendMessage(
                        new TranslatableText("msg.shutter.headline.cmd.success"),
                        new TranslatableText("msg.shutter.ok.showing_path"),
                        Messaging.MessageType.NEUTRAL);
            else
                Messaging.sendMessage(
                        new TranslatableText("msg.shutter.headline.cmd.success"),
                        new TranslatableText("msg.shutter.ok.hiding_path"),
                        Messaging.MessageType.NEUTRAL);

            return 1;
        } catch(PathTooSmallException e) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.not_enough_show"),
                    Messaging.MessageType.NEGATIVE);
            return 0;
        }
    }
}
