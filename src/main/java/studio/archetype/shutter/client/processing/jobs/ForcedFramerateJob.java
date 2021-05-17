package studio.archetype.shutter.client.processing.jobs;

import net.minecraft.client.MinecraftClient;
import studio.archetype.shutter.client.processing.Pipeline;
import studio.archetype.shutter.client.processing.processors.FfmpegVideoProcess;
import studio.archetype.shutter.util.ScreenSize;

public class ForcedFramerateJob {

    private final Pipeline pipeline;
    private final int framerate;
    private final ScreenSize size;

    public ForcedFramerateJob(int framerate, double pathLength, String name) {
        this.framerate = framerate;
        this.size = new ScreenSize(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight());
        this.pipeline = Pipeline.getDefaultPipeline((int)(this.framerate * pathLength), this.size, new FfmpegVideoProcess(name, this.size));
        this.pipeline.run();
    }
}
