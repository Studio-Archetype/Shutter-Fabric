package studio.archetype.shutter.util;

import studio.archetype.shutter.Shutter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}
