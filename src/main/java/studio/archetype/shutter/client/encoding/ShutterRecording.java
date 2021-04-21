package studio.archetype.shutter.client.encoding;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ShutterRecording {

    private static final Path RECORD_DIR = FabricLoader.getInstance().getGameDir().resolve("shutter").resolve("recordings");

    private PathFrameProducer currentProducer;

    public void start(String filename, int framerate, int width, int height) {
        /*this.currentProducer = new PathFrameProducer(framerate, width, height);
        FFmpeg.atPath()
                .addInput(FrameInput.withProducer(currentProducer).setFrameSize(width, height).setFrameRate(framerate))
                .addOutput(UrlOutput.toPath(RECORD_DIR.resolve(filename)).setFrameRate(framerate))
                .setOverwriteOutput(true)
                .execute();*/
    }

    public void stop() {
        //this.currentProducer.stop();
    }
}
