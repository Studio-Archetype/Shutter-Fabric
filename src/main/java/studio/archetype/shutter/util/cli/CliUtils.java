package studio.archetype.shutter.util.cli;

import com.google.common.collect.Lists;
import studio.archetype.shutter.client.config.SaveFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
                if(runBlockingCommand(command,SaveFile.SHUTTER_DIR.toFile(), false, p) == 0)
                    return true;
            } catch(IOException | InterruptedException ignored) { }
        }
        return false;
    }

    public static Process createCommandProcess(String command, File directory, boolean redirectError, CommandProperty... properties) throws IOException {
        List<String> args = Lists.newArrayList(command);
        Stream.of(properties).forEach(p -> args.addAll(Lists.newArrayList(p.toString().split(" "))));
        return new ProcessBuilder(args).redirectErrorStream(redirectError).directory(directory).start();
    }

    public static int runBlockingCommand(String command, File directory, boolean redirectError, CommandProperty... properties) throws IOException, InterruptedException {
        Process process = createCommandProcess(command, directory, redirectError, properties);
        try(BufferedReader reader = new BufferedReader (new InputStreamReader(process.getInputStream()))) {
            while (process.isAlive())
                reader.readLine();
        }

        return process.waitFor();
    }

    public static CompletableFuture<Integer> runCommandAsync(String command, File directory, boolean redirectError, CommandProperty... properties) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return runBlockingCommand(command, directory, redirectError, properties);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return -1;
            }
        });
    }


}
