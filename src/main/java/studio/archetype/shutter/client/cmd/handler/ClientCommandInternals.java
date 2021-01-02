package studio.archetype.shutter.client.cmd.handler;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandException;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static studio.archetype.shutter.client.cmd.handler.ClientCommandManager.DISPATCHER;

public final class ClientCommandInternals {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final char PREFIX = '/';

    public static boolean executeCommand(String message) {
        if (message.isEmpty())
            return false;

        if (message.charAt(0) != PREFIX)
            return false;

        MinecraftClient client = MinecraftClient.getInstance();

        FabricClientCommandSource commandSource = (FabricClientCommandSource) client.getNetworkHandler().getCommandSource();

        client.getProfiler().push(message);

        try {
            DISPATCHER.execute(message.substring(1), commandSource);
            return true;
        } catch (CommandSyntaxException e) {
            LOGGER.warn("Syntax exception for client-sided command '{}'", message, e);
            if (isIgnoredException(e.getType()))
                return false;
            commandSource.sendError(getErrorMessage(e));
            return true;
        } catch (CommandException e) {
            LOGGER.warn("Error while executing client-sided command '{}'", message, e);
            commandSource.sendError(e.getTextMessage());
            return true;
        } catch (RuntimeException e) {
            LOGGER.warn("Error while executing client-sided command '{}'", message, e);
            commandSource.sendError(Text.of(e.getMessage()));
            return true;
        } finally {
            client.getProfiler().pop();
        }
    }

    private static boolean isIgnoredException(CommandExceptionType type) {
        BuiltInExceptionProvider builtIn = CommandSyntaxException.BUILT_IN_EXCEPTIONS;

        return type == builtIn.dispatcherUnknownCommand() || type == builtIn.dispatcherParseException();
    }

    private static Text getErrorMessage(CommandSyntaxException e) {
        Text message = Texts.toText(e.getRawMessage());
        String context = e.getContext();

        return context != null ? new TranslatableText("command.context.parse_error", message, context) : message;
    }

    public static void checkDispatcher() {
        // noinspection CodeBlock2Expr
        DISPATCHER.findAmbiguities((parent, child, sibling, inputs) -> {
            LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", DISPATCHER.getPath(child), DISPATCHER.getPath(sibling), inputs);
        });
    }

    public static void addCommands(CommandDispatcher<FabricClientCommandSource> target, FabricClientCommandSource source) {
        Map<CommandNode<FabricClientCommandSource>, CommandNode<FabricClientCommandSource>> originalToCopy = new HashMap<>();
        originalToCopy.put(DISPATCHER.getRoot(), target.getRoot());
        copyChildren(DISPATCHER.getRoot(), target.getRoot(), source, originalToCopy);
    }

    private static void copyChildren(CommandNode<FabricClientCommandSource> origin, CommandNode<FabricClientCommandSource> target, FabricClientCommandSource source, Map<CommandNode<FabricClientCommandSource>, CommandNode<FabricClientCommandSource>> originalToCopy) {
        for (CommandNode<FabricClientCommandSource> child : origin.getChildren()) {
            if (!child.canUse(source)) continue;

            ArgumentBuilder<FabricClientCommandSource, ?> builder = child.createBuilder();

            builder.requires(it -> true);

            if (builder.getCommand() != null)
                builder.executes(context -> 0);

            if (builder.getRedirect() != null)
                builder.redirect(originalToCopy.get(builder.getRedirect()));

            CommandNode<FabricClientCommandSource> result = builder.build();
            originalToCopy.put(child, result);
            target.addChild(result);

            if (!child.getChildren().isEmpty())
                copyChildren(child, result, source, originalToCopy);
        }
    }
}