package studio.archetype.shutter.client.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.exceptions.PathTooSmallException;

import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.literal;

public final class PathVisualCommands {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("s")
                        .requires(src -> src.hasPermissionLevel(4))
                        .then(literal("show")
                                .executes(PathVisualCommands::showPath))
                        .then(literal("hide")
                                .executes(PathVisualCommands::hidePath))
                        .then(literal("toggleVisualization")
                                .executes(PathVisualCommands::togglePath)));
    }

    private static int showPath(CommandContext<FabricClientCommandSource> ctx) {
        ClientPlayerEntity p = ctx.getSource().getPlayer();
        Identifier id = CameraPathManager.DEFAULT_PATH;
        CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(p.getEntityWorld());
        try {
            if(manager.isVisualizing())
                ctx.getSource().sendFeedback(Text.of("Path is already being visualized."));
            else {
                manager.togglePathVisualization(id);
                ctx.getSource().sendFeedback(Text.of("Visualizing path."));
            }

            return 1;
        } catch(PathTooSmallException e) {
            ctx.getSource().sendError(Text.of("Not enough nodes in path! The minimum is 2."));
            return 0;
        }
    }

    private static int hidePath(CommandContext<FabricClientCommandSource> ctx) {
        ClientPlayerEntity p = ctx.getSource().getPlayer();
        Identifier id = CameraPathManager.DEFAULT_PATH;
        CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(p.getEntityWorld());
        try {
            if(manager.isVisualizing()) {
                manager.togglePathVisualization(id);
                ctx.getSource().sendFeedback(Text.of("Hiding visualization for path."));
            } else
                ctx.getSource().sendFeedback(Text.of("Path is not being visualized."));

            return 1;
        } catch(PathTooSmallException ignored) { }

        return 0;
    }

    private static int togglePath(CommandContext<FabricClientCommandSource> ctx) {
        ClientPlayerEntity p = ctx.getSource().getPlayer();
        Identifier id = CameraPathManager.DEFAULT_PATH;
        CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(p.getEntityWorld());
        try {
            if(manager.togglePathVisualization(id))
                ctx.getSource().sendFeedback(Text.of("Visualizing path."));
            else
                ctx.getSource().sendFeedback(Text.of("Hiding visualization for path."));

            return 1;
        } catch(PathTooSmallException e) {
            ctx.getSource().sendError(Text.of("Not enough nodes in path! The minimum is 2."));
            return 0;
        }
    }
}
