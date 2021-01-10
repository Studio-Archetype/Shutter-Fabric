package studio.archetype.shutter.client.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.gui.ConfigScreenProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;
import studio.archetype.shutter.client.config.ClientConfig;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.ui.Messaging;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.exceptions.PathEmptyException;
import studio.archetype.shutter.pathing.exceptions.PathNotFollowingException;
import studio.archetype.shutter.pathing.exceptions.PathTooSmallException;

import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.argument;
import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.literal;

public final class PathControlCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(
                literal("s")
                    .requires(src -> src.hasPermissionLevel(4))
                    .then(literal("start")
                            .executes(ctx -> startPath(ctx, ClientConfigManager.CLIENT_CONFIG.genSettings.pathTime))
                            .then(argument("time", PathTimeArgumentType.pathTime())
                                    .executes(ctx -> startPath(ctx, PathTimeArgumentType.getTicks(ctx, "time")))))
                    .then(literal("stop")
                            .executes(PathControlCommand::stopPath))
                    .then(literal("clear")
                            .executes(PathControlCommand::clearPath))
                    .then(literal("config")
                            .executes(PathControlCommand::openConfig)));

        dispatcher.register(
                literal("shutter")
                    .redirect(node));
    }

    private static int startPath(CommandContext<FabricClientCommandSource> ctx, double pathTime) {
        try {
            CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(ctx.getSource().getWorld());
            ClientConfigManager.CLIENT_CONFIG.genSettings.pathTime = pathTime;
            manager.startCameraPath(CameraPathManager.DEFAULT_PATH, pathTime);
            return 1;
        } catch(PathTooSmallException e) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.not_enough_start"),
                    Messaging.MessageType.NEGATIVE);
            return 0;
        }
    }

    private static int stopPath(CommandContext<FabricClientCommandSource> ctx) {
        try {
            CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(ctx.getSource().getWorld());
            manager.stopCameraPath();
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.success"),
                    new TranslatableText("msg.shutter.ok.stopped_following"),
                    Messaging.MessageType.POSITIVE);
            return 1;
        } catch(PathNotFollowingException e) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.not_following"),
                    Messaging.MessageType.NEUTRAL);
            return 0;
        }
    }

    private static int clearPath(CommandContext<FabricClientCommandSource> ctx) {
        try {
            ShutterClient.INSTANCE.getPathManager(MinecraftClient.getInstance().world).clearPath(CameraPathManager.DEFAULT_PATH);
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.success"),
                    new TranslatableText("msg.shutter.ok.path_cleared"),
                    Messaging.MessageType.POSITIVE);
            return 1;
        } catch(PathEmptyException e) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.path_empty"),
                    Messaging.MessageType.NEUTRAL);
            return 0;
        }
    }

    private static int openConfig(CommandContext<FabricClientCommandSource> ctx) {
        ConfigScreenProvider<ClientConfig> provider = (ConfigScreenProvider<ClientConfig>) AutoConfig.getConfigScreen(ClientConfig.class, ctx.getSource().getClient().currentScreen);
        provider.setOptionFunction((gen, field) -> "config." + Shutter.MOD_ID + "." + field.getName());
        ctx.getSource().getClient().openScreen(provider.get());
        return 1;
    }
}
