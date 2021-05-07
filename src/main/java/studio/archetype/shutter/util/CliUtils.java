package studio.archetype.shutter.util;

import com.google.common.collect.Lists;
import studio.archetype.shutter.client.encoding.CommandProperty;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CliUtils {

    private static final List<CommandProperty> versionFlags = Lists.newArrayList(
            CommandProperty.flag("-v"),
            CommandProperty.flag("--v"),
            CommandProperty.flag("-version"),
            CommandProperty.flag("--version"),
            CommandProperty.flag("/v")
    );

    public static boolean isCommandAvailable(String command) {
        for(CommandProperty p : versionFlags) {
            try {
                if(runCommand(command, p.get()) == 0)
                    return true;
            } catch(IOException | InterruptedException ignored) { }
        }
        return false;
    }

    public static int runCommand(String command, CommandProperty... properties) throws IOException, InterruptedException {
        final String cmd = command + " " + Stream.of(properties).map(CommandProperty::toString).collect(Collectors.joining(" "));
        return Runtime.getRuntime().exec(cmd).waitFor();
    }

    public static CompletableFuture<Integer> runCommandAsync(String command, CommandProperty... properties) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return runCommand(command, properties);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return -1;
            }
        });
    }
}
