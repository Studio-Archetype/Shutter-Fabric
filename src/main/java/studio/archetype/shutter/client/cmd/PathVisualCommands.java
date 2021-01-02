package studio.archetype.shutter.client.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;

import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.literal;

public final class PathVisualCommands {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("s")
                        .requires(src -> src.hasPermissionLevel(4))
                        .then(literal("show")
                                .executes(PathVisualCommands::showPath))
                        .then(literal("hide")
                                .executes(PathVisualCommands::hidePath)));
    }

    private static int showPath(CommandContext<FabricClientCommandSource> ctx) {
        return 1;
    }

    private static int hidePath(CommandContext<FabricClientCommandSource> ctx) {
        return 1;
    }
}
