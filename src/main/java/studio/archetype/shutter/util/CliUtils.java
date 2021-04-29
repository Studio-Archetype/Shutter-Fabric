package studio.archetype.shutter.util;

import studio.archetype.shutter.client.encoding.CommandProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.concurrent.CompletableFuture;
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
        Process p = Runtime.getRuntime().exec(cmd);
        int ret = p.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        reader.lines().forEach(System.out::println);
        reader.close();
        p.destroy();
        return ret;
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
