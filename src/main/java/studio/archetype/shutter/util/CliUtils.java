package studio.archetype.shutter.util;

import studio.archetype.shutter.client.encoding.CommandProperty;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CliUtils {

    public static boolean isCommandAvailable(String command) {
        String[] args = new String[] {"-v", "--v", "-version", "--version", "/v"};
        try {
            for(String a : args)
                if(new ProcessBuilder(command, a).start().waitFor() == 0)
                    return true;
            return false;
        } catch(IOException | InterruptedException e) {
            return false;
        }
    }

    public static int runCommand(String command, CommandProperty... properties) throws IOException, InterruptedException {
        final String cmd = command + " " + Stream.of(properties).map(CommandProperty::toString).collect(Collectors.joining(" "));
        System.out.println("Command: \"" + cmd + "\"");
        return new ProcessBuilder(cmd).start().waitFor();
    }

    public static CompletableFuture<Integer> runCommandAsync(String command, CommandProperty... properties) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return runCommand(command, properties);
            } catch (IOException | InterruptedException e) {
                return -1;
            }
        });
    }
}
