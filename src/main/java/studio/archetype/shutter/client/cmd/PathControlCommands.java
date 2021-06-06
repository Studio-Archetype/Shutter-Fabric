package studio.archetype.shutter.client.cmd;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Pair;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.config.ClientConfig;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.config.enums.RecordingMode;
import studio.archetype.shutter.client.processing.jobs.Jobs;
import studio.archetype.shutter.client.ui.Messaging;
import studio.archetype.shutter.client.ui.screens.RecordingScreen;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.exceptions.PathEmptyException;
import studio.archetype.shutter.pathing.exceptions.PathNotFollowingException;
import studio.archetype.shutter.pathing.exceptions.PathTooSmallException;
import studio.archetype.shutter.util.cli.CliUtils;

import java.util.*;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public final class PathControlCommands {

    private static Pair<Integer, String> countdown;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(
                literal("s")
                    .requires(src -> src.hasPermissionLevel(4))
                    .then(literal("help")
                            .executes(PathControlCommands::printHelp))
                    .then(literal("?")
                            .executes(PathControlCommands::printHelp))
                    .then(literal("record")
                        .executes(ctx ->  {
                            ctx.getSource().getClient().openScreen(new RecordingScreen(ClientConfigManager.FFMPEG_CONFIG));
                            return 1;
                        })
                        .then(literal("abort")
                            .executes(PathControlCommands::abortRecording)))
                    .then(literal("start")
                            .executes(ctx -> startPath(ctx, ClientConfigManager.CLIENT_CONFIG.genSettings.pathTime, false))
                            .then(argument("loop", BoolArgumentType.bool())
                                    .executes(ctx -> startPath(ctx, ClientConfigManager.CLIENT_CONFIG.genSettings.pathTime, BoolArgumentType.getBool(ctx, "loop"))))
                            .then(argument("time", PathTimeArgumentType.pathTime())
                                    .executes(ctx -> startPath(ctx, PathTimeArgumentType.getTicks(ctx, "time"), false))
                                        .then(argument("loop", BoolArgumentType.bool())
                                            .executes(ctx -> startPath(ctx, PathTimeArgumentType.getTicks(ctx, "time"), BoolArgumentType.getBool(ctx, "loop"))))))
                    .then(literal("offset")
                            .executes(PathControlCommands::offsetPath))
                    .then(literal("stop")
                            .executes(PathControlCommands::stopPath))
                    .then(literal("clear")
                            .executes(PathControlCommands::clearPath))
                    .then(literal("config")
                            .executes(PathControlCommands::openConfig)));

        dispatcher.register(
                literal("shutter")
                    .redirect(node));

    }

    private static int printHelp(CommandContext<FabricClientCommandSource> ctx) {
        ctx.getSource().sendFeedback(new TranslatableText("msg.shutter.help.header"));

        sendCommandHelpLine(ctx.getSource(),
                "/s select",
                "msg.shutter.help.cmd.select",
                Collections.singletonMap("path", true));
        sendCommandHelpLine(ctx.getSource(),
                "/s start",
                "msg.shutter.help.cmd.start",
                ImmutableMap.of("time", true, "loop", true));
        sendCommandHelpLine(ctx.getSource(),
                "/s stop",
                "msg.shutter.help.cmd.stop",
                null);
        sendCommandHelpLine(ctx.getSource(),
                "/s record",
                "msg.shutter.help.cmd.record",
                null);
        sendCommandHelpLine(ctx.getSource(),
                "/s clear",
                "msg.shutter.help.cmd.clear",
                null);
        sendCommandHelpLine(ctx.getSource(),
                "/s offset",
                "msg.shutter.help.cmd.offset",
                null);

        ctx.getSource().sendFeedback(new TranslatableText("msg.shutter.help.line"));

        sendCommandHelpLine(ctx.getSource(),
                "/s add",
                "msg.shutter.help.cmd.add",
                null);
        sendCommandHelpLine(ctx.getSource(),
                "/s set",
                "msg.shutter.help.cmd.set",
                Collections.singletonMap("index", false));
        sendCommandHelpLine(ctx.getSource(),
                "/s remove",
                "msg.shutter.help.cmd.remove",
                Collections.singletonMap("index", false));
        sendCommandHelpLine(ctx.getSource(),
                "/s goto",
                "msg.shutter.help.cmd.goto",
                Collections.singletonMap("index", false));

        ctx.getSource().sendFeedback(new TranslatableText("msg.shutter.help.line"));

        sendCommandHelpLine(ctx.getSource(),
                "/s show",
                "msg.shutter.help.cmd.show",
                Collections.singletonMap("loop", true));
        sendCommandHelpLine(ctx.getSource(),
                "/s hide",
                "msg.shutter.help.cmd.hide",
                null);
        sendCommandHelpLine(ctx.getSource(),
                "/s toggle",
                "msg.shutter.help.cmd.toggle",
                Collections.singletonMap("loop", true));
        sendCommandHelpLine(ctx.getSource(),
                "/s config",
                "msg.shutter.help.cmd.config",
                null);

        ctx.getSource().sendFeedback(new TranslatableText("msg.shutter.help.line"));

        sendCommandHelpLine(ctx.getSource(),
                "/s export",
                "msg.shutter.help.cmd.export",
                Collections.singletonMap("file", true));
        /*sendCommandHelpLine(ctx.getSource(),
                "/s upload",
                "msg.shutter.help.cmd.upload",
                Collections.singletonMap("file", true));*/
        sendCommandHelpLine(ctx.getSource(),
                "/s import",
                "msg.shutter.help.cmd.import",
                 ImmutableMap.of("file", false, "relative", true));
        /*sendCommandHelpLine(ctx.getSource(),
                "/s download",
                "msg.shutter.help.cmd.download",
                Collections.singletonMap("url", false));*/

        ctx.getSource().sendFeedback(new TranslatableText("msg.shutter.help.line"));

        return 1;
    }

    private static int startPath(CommandContext<FabricClientCommandSource> ctx, double pathTime, boolean loop) {
        try {
            CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(ctx.getSource().getWorld());
            ClientConfigManager.CLIENT_CONFIG.genSettings.pathTime = pathTime;
            if(manager.isVisualizing() && loop) {
                manager.togglePathVisualization(true);
                manager.togglePathVisualization(true);
            }
            manager.startCameraPath(pathTime, loop);
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
            ShutterClient.INSTANCE.getPathManager(MinecraftClient.getInstance().world).clearPath(false);
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

    private static int offsetPath(CommandContext<FabricClientCommandSource> ctx) {
        CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(ctx.getSource().getWorld());
        if(manager.getCurrentPath().getNodes().size() == 0) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.offset_empty"),
                    Messaging.MessageType.NEGATIVE);
            return 1;
        }

        Vec3d pos = ctx.getSource().getClient().gameRenderer.getCamera().getPos();
        manager.getCurrentPath().offset(pos);

        Messaging.sendMessage(
                new TranslatableText("msg.shutter.headline.cmd.success"),
                new TranslatableText("msg.shutter.ok.offset"),
                new LiteralText("x").formatted(Formatting.DARK_RED)
                        .append(new LiteralText(String.format("%.2f",pos.x)).formatted(Formatting.RED, Formatting.UNDERLINE))
                        .append(new LiteralText(" y").formatted(Formatting.DARK_GREEN))
                        .append(new LiteralText(String.format("%.2f", pos.y)).formatted(Formatting.GREEN, Formatting.UNDERLINE))
                        .append(new LiteralText(" z").formatted(Formatting.DARK_BLUE))
                        .append(new LiteralText(String.format("%.2f", pos.z)).formatted(Formatting.BLUE, Formatting.UNDERLINE)),
                Messaging.MessageType.POSITIVE);
        return 1;
    }

    private static void sendCommandHelpLine(FabricClientCommandSource source, String command, String descriptor, Map<String, Boolean> arguments) {
        MutableText hover = new LiteralText("");
        if(arguments != null) {
            List<Text> args = new ArrayList<>();
            arguments.forEach((a, d) -> {
                String arg = "msg.shutter.help.arg." + a;
                args.add(new LiteralText(d ? "[" : "<")
                        .append(new TranslatableText(arg))
                        .append(new LiteralText(d ? "]: " : ">: "))
                        .append(new TranslatableText(arg + ".desc")));
            });
            Iterator<Text> argsIt = args.listIterator();
            while (argsIt.hasNext()) {
                hover.append(argsIt.next());
                if (argsIt.hasNext())
                    hover.append(new LiteralText("\n"));
            }
            argsIt.remove();
            args.clear();
        } else {
            hover.append(new TranslatableText("msg.shutter.help.arg.none"));
        }

        MutableText ult = new LiteralText("§2" + command + " §8- §r")
                .append(new TranslatableText(descriptor))
                .setStyle(Style.EMPTY
                        .withHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.formatted(Formatting.GRAY, Formatting.ITALIC)))
                        .withColor(Formatting.GRAY));

        source.sendFeedback(ult);
    }

    private static int openConfig(CommandContext<FabricClientCommandSource> ctx) {
        ConfigScreenProvider<ClientConfig> provider = (ConfigScreenProvider<ClientConfig>) AutoConfig.getConfigScreen(ClientConfig.class, ctx.getSource().getClient().currentScreen);
        provider.setOptionFunction((gen, field) -> "config." + Shutter.MOD_ID + "." + field.getName());
        ctx.getSource().getClient().openScreen(provider.get());
        return 1;
    }

    private static int abortRecording(CommandContext<FabricClientCommandSource> ctx) {
        try {
            if(Jobs.abortRecording(ShutterClient.INSTANCE.getPathManager(ctx.getSource().getWorld())))
                Messaging.sendMessage(
                        new TranslatableText("msg.shutter.headline.cmd.success"),
                        new TranslatableText("msg.shutter.ok.abort_rec"),
                        Messaging.MessageType.POSITIVE);
            else
                Messaging.sendMessage(
                        new TranslatableText("msg.shutter.headline.cmd.failed"),
                        new TranslatableText("msg.shutter.error.abort_fail"),
                        Messaging.MessageType.NEUTRAL);
        } catch (PathNotFollowingException ignored) { }
        return 1;
    }
}
