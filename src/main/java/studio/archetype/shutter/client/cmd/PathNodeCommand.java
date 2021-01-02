package studio.archetype.shutter.client.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;

import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.literal;
import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.argument;

public final class PathNodeCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
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
    }

    private static int addNode(CommandContext<FabricClientCommandSource> source) {
        return 1;
    }

    private static int removeNode(CommandContext<FabricClientCommandSource> source, int index) {
        return 1;
    }

    private static int setNode(CommandContext<FabricClientCommandSource> source, int index) {
        return 1;
    }

    private static int gotoNode(CommandContext<FabricClientCommandSource> source, int index) {
        return 1;
    }
}
