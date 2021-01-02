package studio.archetype.shutter.client.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.exceptions.PathEmptyException;

import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.literal;

public final class PathControlCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("s")
                    .requires(src -> src.hasPermissionLevel(4))
                    .then(literal("stop")
                            .executes(PathControlCommand::stopPath))
                    .then(literal("clear")
                            .executes(PathControlCommand::clearPath))
                    .then(literal("config")
                            .executes(PathControlCommand::openConfig)));
    }

    //TODO START PATH

    private static int stopPath(CommandContext<FabricClientCommandSource> source) {
        return 1;
    }

    private static int clearPath(CommandContext<FabricClientCommandSource> source) {
        try {
            ShutterClient.INSTANCE.getPathManager(MinecraftClient.getInstance().world).clearPath(CameraPathManager.DEFAULT_PATH);
            source.getSource().sendFeedback(Text.of("Path has been cleared."));
            return 1;
        } catch(PathEmptyException e) {
            source.getSource().sendError(Text.of("Path is already empty!"));
            return 0;
        }
    }

    private static int openConfig(CommandContext<FabricClientCommandSource> source) {
        return 1;
    }
}
