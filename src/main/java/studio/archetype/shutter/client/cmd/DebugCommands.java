package studio.archetype.shutter.client.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import studio.archetype.shutter.client.ShutterClient;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public final class DebugCommands {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(
                literal("s")
                        .requires(src -> src.hasPermissionLevel(4))
                        .then(literal("framerate")
                                .then(argument("framerate", IntegerArgumentType.integer())
                                        .executes(ctx -> setFramerate(IntegerArgumentType.getInteger(ctx, "framerate"))))));

        dispatcher.register(
                literal("shutter")
                        .redirect(node));
    }

    private static int setFramerate(int framerate) {
        if(framerate == 0)
            ShutterClient.INSTANCE.getFramerateController().stopControlling();
        else
            ShutterClient.INSTANCE.getFramerateController().startControlling(framerate, false);

        return 1;
    }
}
