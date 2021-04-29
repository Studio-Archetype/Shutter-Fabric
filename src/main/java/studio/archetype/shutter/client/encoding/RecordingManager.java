package studio.archetype.shutter.client.encoding;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.config.ClientConfig;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.ui.Messaging;
import studio.archetype.shutter.util.CliUtils;
import studio.archetype.shutter.util.FramebufferUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RecordingManager {

    private static final Path RECORD_DIR = FabricLoader.getInstance().getGameDir().resolve("shutter").resolve("recordings");

    private int targetFramerate, currentFramecounter, totalFramecount;
    private float tickDelta;
    private boolean isFrameQueued, hasTicked, hasServerTicked = false;
    private String filename;
    private volatile CompletableFuture<Integer> currentEncodingJob;

    public RecordingManager() {
        this.targetFramerate = this.currentFramecounter = 0;
        ClientTickEvents.START_CLIENT_TICK.register((e) -> onTick());
    }

    public void initRecording(int framerate, String filename) {
        this.targetFramerate = framerate;
        this.currentFramecounter = this.totalFramecount = 0;
        this.tickDelta = 1.0F / targetFramerate;
        this.filename = filename;
        if(!filename.equals(""))
            RECORD_DIR.resolve(filename).toFile().mkdirs();
    }

    public boolean isRecording() {
        return targetFramerate != 0;
    }

    public boolean isEncoding() {
        return currentEncodingJob != null;
    }

    public int processTick(RenderTickCounter tickCounter, long timeMillis) {
        if(!isRecording())
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
        return isRecording() && isFrameQueued;
    }

    public boolean isServerTickValid() {
        if(!isRecording())
            return true;

        if(currentFramecounter % (targetFramerate / 20) == 0 && !hasServerTicked) {
            hasServerTicked = true;
            return true;
        }

        return false;
    }

    public void updateTimings(Framebuffer buffer) {
        if(isFrameQueued || !isRecording())
            return;

        scheduleCapture(buffer);
        hasTicked = hasServerTicked = false;
        currentFramecounter++;

        if(currentFramecounter >= targetFramerate)
            this.currentFramecounter = 0;
    }

    public void finishRecording() {
        if(isRecording()) {
            this.targetFramerate = 0;
            this.currentFramecounter = this.totalFramecount = 0;
            if(ClientConfigManager.CLIENT_CONFIG.recSettings.renderMode != ClientConfig.RecordingMode.FRAMES) {
                //TODO: Actual parameters
                this.currentEncodingJob = CliUtils.runCommandAsync("ffmpeg",
                        FfmpegProperties.FRAMERATE.get(60));
                Messaging.sendMessage(
                        new TranslatableText("msg.shutter.headline.cmd.success"),
                        new TranslatableText("msg.shutter.ok.recording_start"),
                        Messaging.MessageType.NEUTRAL);
            }
        }
    }

    private void onTick() {
        if(isEncoding()) {
            if(currentEncodingJob.isDone()){
                try {
                    if(this.currentEncodingJob.get() == 0)
                        Messaging.sendMessage(
                                new TranslatableText("msg.shutter.headline.cmd.success"),
                                new TranslatableText("msg.shutter.ok.recording_done"),
                                Messaging.MessageType.POSITIVE);
                    else
                        Messaging.sendMessage(
                                new TranslatableText("msg.shutter.headline.cmd.failed"),
                                new TranslatableText("msg.shutter.error.recording_error", this.currentEncodingJob.get()),
                                Messaging.MessageType.NEGATIVE);

                    if(ClientConfigManager.CLIENT_CONFIG.recSettings.renderMode == ClientConfig.RecordingMode.VIDEO)
                        FileUtils.deleteDirectory(RECORD_DIR.resolve(this.filename).toFile());
                    this.currentEncodingJob.cancel(true);
                    this.currentEncodingJob = null;
                } catch(IOException | InterruptedException | ExecutionException ignored) { }
            }
        }
    }

    private void scheduleCapture(Framebuffer buffer) {
        this.isFrameQueued = true;
        RenderSystem.recordRenderCall(() -> ShutterClient.INSTANCE.getFramerateHandler().applyCapture(buffer));
    }

    private void applyCapture(Framebuffer buffer) {
        this.isFrameQueued = false;
        String filename = "frame_" + StringUtils.leftPad(String.valueOf(totalFramecount), 6, '0') + ".png";
        FramebufferUtils.framebufferToFile(buffer, RECORD_DIR.resolve(this.filename).toFile(), filename);
        totalFramecount++;
    }
}
