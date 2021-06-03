package studio.archetype.shutter.client.processing.jobs;

import net.minecraft.client.MinecraftClient;
import studio.archetype.shutter.client.events.WorldRenderedCallback;
import studio.archetype.shutter.client.processing.processors.FfmpegVideoProcessor;
import studio.archetype.shutter.util.ScreenSize;

import java.io.IOException;

//This is absolute shitcode designed as a quick solution without having to write a full job queue system. Ignore the badness.
public final class Jobs {

    private static Pipeline<?, ?, ?, ?, ?> currentPipeline;

    public static void init() {
        WorldRenderedCallback.EVENT.register(buffer -> onRenderEvent());
    }

    public static void createNewJob(int framerate, String name) {
        if(currentPipeline == null) {
            ScreenSize size = new ScreenSize(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight());
            try {
                currentPipeline = Pipeline.getDefaultPipeline(framerate, size, new FfmpegVideoProcessor(name, size));
                currentPipeline.setup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void onRenderEvent() {
        if(currentPipeline != null) {
            if(currentPipeline.onRender()) {
                currentPipeline.close();
                currentPipeline = null;
            }
        }
    }
}
