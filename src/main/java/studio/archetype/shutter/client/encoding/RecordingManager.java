package studio.archetype.shutter.client.encoding;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderTickCounter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.util.CliUtils;
import studio.archetype.shutter.util.FramebufferUtils;

import java.nio.file.Path;

public class RecordingManager {

    private static final Path RECORD_DIR = FabricLoader.getInstance().getGameDir().resolve("shutter").resolve("recordings");

    private boolean hasFoundFfmpeg;

    private int targetFramerate, currentFramecounter;
    private float tickDelta;
    private boolean isFrameQueued, hasTicked, hasServerTicked = false;

    public RecordingManager() {
        this.targetFramerate = this.currentFramecounter = 0;
        this.hasFoundFfmpeg = CliUtils.isCommandAvailable("ffmpeg");
    }

    public void syncRenderingAndTicks(int framerate) {
        this.targetFramerate = framerate;
        this.currentFramecounter = 0;
        this.tickDelta = 1.0F / targetFramerate;
    }

    public int processTick(RenderTickCounter tickCounter, long timeMillis) {
        if(targetFramerate == 0)
            return tickCounter.beginRenderTick(timeMillis);

        tickCounter.beginRenderTick(timeMillis);

        if(!isFrameQueued) {
            tickCounter.lastFrameDuration = 1000.0F / targetFramerate;
            tickCounter.tickDelta = this.tickDelta;
        } else {
            tickCounter.tickDelta = 0;
            tickCounter.lastFrameDuration = 0;
        }

        if(currentFramecounter % (targetFramerate / 20) == 0 && !hasTicked) {
            hasTicked = true;
            return 1;
        }

        return 0;
    }

    public boolean skipRenderTick() {
        return targetFramerate != 0 && isFrameQueued;
    }

    public boolean isServerTickValid() {
        if(targetFramerate == 0)
            return true;

        if(currentFramecounter % (targetFramerate / 20) == 0 && !hasServerTicked) {
            hasServerTicked = true;
            return true;
        }

        return false;
    }

    public void updateTimings(Framebuffer buffer) {
        if(isFrameQueued || targetFramerate == 0)
            return;

        scheduleCapture(buffer);
        hasTicked = hasServerTicked = false;
        currentFramecounter++;

        if(currentFramecounter >= targetFramerate)
            this.currentFramecounter = 0;
    }


    private void scheduleCapture(Framebuffer buffer) {
        this.isFrameQueued = true;
        RenderSystem.recordRenderCall(() -> ShutterClient.INSTANCE.getFramerateHandler().applyCapture(buffer));
    }

    private int imageCount = 0;
    private void applyCapture(Framebuffer buffer) {
        this.isFrameQueued = false;
        if(imageCount % 5 == 0)
            FramebufferUtils.framebufferToFile(buffer, RECORD_DIR.toFile(), "frame_" + imageCount / 5 + ".png");
        imageCount++;
    }
}
