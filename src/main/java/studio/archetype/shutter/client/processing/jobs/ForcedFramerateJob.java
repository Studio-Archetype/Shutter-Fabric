package studio.archetype.shutter.client.processing.jobs;

import net.minecraft.client.MinecraftClient;
import studio.archetype.shutter.client.processing.processors.FfmpegVideoProcessor;
import studio.archetype.shutter.util.ScreenSize;

import java.io.IOException;

public class ForcedFramerateJob {

    private final int framerate;
    private final ScreenSize size;

    public ForcedFramerateJob(int framerate, double pathLength, String name) {
        this.framerate = framerate;
        this.size = new ScreenSize(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight());
        try {
            Pipeline.getDefaultPipeline((int) (this.framerate * pathLength), this.size, new FfmpegVideoProcessor(name, this.size)).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
