package studio.archetype.shutter.client.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;
import studio.archetype.shutter.client.ui.Messaging;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.CameraPathManager;

import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.argument;
import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.literal;

public final class PathManagementCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(
                literal("s")
                    .requires(src -> src.hasPermissionLevel(4))
                    .then(literal("select")
                        .executes(ctx -> selectPath(ctx, CameraPathManager.DEFAULT_PATH))
                        .then(argument("path", StringArgumentType.word())
                            .executes(ctx -> selectPath(ctx, Shutter.id(StringArgumentType.getString(ctx, "path"))))))
                    .then(literal("export")
                        .then(argument("filename", StringArgumentType.word())
                            .executes(ctx -> exportFile(ctx, StringArgumentType.getString(ctx, "filename")))))
                    .then(literal("upload")
                        .then(argument("filename", StringArgumentType.word())
                            .executes(ctx -> uploadFile(ctx, StringArgumentType.getString(ctx, "filename"))))));

        dispatcher.register(
                literal("shutter")
                        .redirect(node));
    }

    private static int selectPath(CommandContext<FabricClientCommandSource> ctx, Identifier id) {
        CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(ctx.getSource().getWorld());
        boolean isNew = manager.hasPath(id);

        if(!isNew) {
            manager.setCurrentPath(id);
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.success"),
                    new TranslatableText("msg.shutter.ok.select_new", id.getPath()),
                    Messaging.MessageType.POSITIVE);
        } else {
            if(manager.setCurrentPath(id)) {
                if(id.equals(CameraPathManager.DEFAULT_PATH))
                    Messaging.sendMessage(
                            new TranslatableText("msg.shutter.headline.cmd.success"),
                            new TranslatableText("msg.shutter.ok.select_default"),
                            Messaging.MessageType.POSITIVE);
                else
                    Messaging.sendMessage(
                            new TranslatableText("msg.shutter.headline.cmd.success"),
                            new TranslatableText("msg.shutter.ok.select", id.getPath()),
                            Messaging.MessageType.POSITIVE);
            } else {
                Messaging.sendMessage(
                        new TranslatableText("msg.shutter.headline.cmd.failed"),
                        new TranslatableText("msg.shutter.error.select_already", id.getPath()),
                        Messaging.MessageType.NEUTRAL);
            }
        }

        return 1;
    }

    private static int exportFile(CommandContext<FabricClientCommandSource> ctx, String name) {
        CameraPath path = ShutterClient.INSTANCE.getPathManager(ctx.getSource().getWorld()).getCurrentPath();
        if(path.export(name)) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.success"),
                    new TranslatableText("msg.shutter.ok.export", name + ".json"),
                    Messaging.MessageType.POSITIVE);
        } else {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.export"),
                    Messaging.MessageType.NEGATIVE);
        }
        return 1;
    }

    private static int uploadFile(CommandContext<FabricClientCommandSource> ctx, String name) {
        CameraPath path = ShutterClient.INSTANCE.getPathManager(ctx.getSource().getWorld()).getCurrentPath();

        if(!path.exportHastebin(name)) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.upload"),
                    Messaging.MessageType.NEGATIVE);
            ctx.getSource().sendError(new TranslatableText("msg.shutter.error.hastebin", "Failed to encode Path!"));
        }

        return 1;
    }
}
