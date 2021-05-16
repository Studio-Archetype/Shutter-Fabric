package studio.archetype.shutter.client.processing;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.config.SaveFile;
import studio.archetype.shutter.client.config.enums.RecordingMode;
import studio.archetype.shutter.client.ui.Messaging;
import studio.archetype.shutter.client.ui.ShutterMessageToast;
import studio.archetype.shutter.client.ui.ShutterWaitingToast;
import studio.archetype.shutter.util.AsyncUtils;
import studio.archetype.shutter.util.CliUtils;
import studio.archetype.shutter.util.FramebufferUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RecordingManager {

    private static final Path RECORD_DIR = FabricLoader.getInstance().getGameDir().resolve("shutter").resolve("recordings");

    private int targetFramerate, currentFramecounter, totalFramecount;
    private float tickDelta;
    private boolean isFrameQueued, hasTicked, hasServerTicked = false;

    private String filename;
    private ShutterWaitingToast toast;

    private final Consumer<Integer> onEncodingFinish = (i) -> {
        if(i == 0)
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.success"),
                    new TranslatableText("msg.shutter.ok.recording_done"),
                    Messaging.MessageType.POSITIVE);
        else
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.recording_error", i),
                    Messaging.MessageType.NEGATIVE);

        finishEncoding();
    };
    private final Consumer<Exception> onEncodingError = (e) -> {
        Messaging.sendMessage(
                new TranslatableText("msg.shutter.headline.cmd.failed"),
                new TranslatableText("msg.shutter.error.recording_error"),
                new LiteralText(e.getClass().getSimpleName()),
                Messaging.MessageType.NEGATIVE);

        finishEncoding();
    };

    public RecordingManager() {
        this.targetFramerate = this.currentFramecounter = 0;
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

    private void scheduleCapture(Framebuffer buffer) {
        this.isFrameQueued = true;
        RenderSystem.recordRenderCall(() -> ShutterClient.INSTANCE.getFramerateHandler().applyCapture(buffer));
    }

    private void applyCapture(Framebuffer buffer) {
        this.isFrameQueued = false;
        String filename = "frame_" + StringUtils.leftPad(String.valueOf(totalFramecount), 4, '0') + ".png";
        FramebufferUtils.framebufferToFile(buffer, RECORD_DIR.resolve(this.filename).toFile(), filename);
        totalFramecount++;
    }

    public void finishRecording() {
        if(isRecording()) {
            this.targetFramerate = 0;
            this.currentFramecounter = this.totalFramecount = 0;
            if(ClientConfigManager.CLIENT_CONFIG.recSettings.renderMode != RecordingMode.FRAMES) {
                AsyncUtils.queueAsync(createFfmpegCommand(), onEncodingFinish, onEncodingError);
                this.toast = new ShutterWaitingToast(
                        ShutterMessageToast.Type.NEUTRAL,
                        new TranslatableText("msg.shutter.ok.recording_start"),
                        new TranslatableText("msg.shutter.ok.recording_wait").formatted(Formatting.RED, Formatting.ITALIC),
                        null);
                MinecraftClient.getInstance().getToastManager().add(this.toast);
            }
        }
    }

    private void finishEncoding() {
        if(ClientConfigManager.CLIENT_CONFIG.recSettings.renderMode == RecordingMode.VIDEO) {
            try {
                FileUtils.deleteDirectory(RECORD_DIR.resolve(this.filename).toFile());
            } catch (IOException ignored) { }
        }
        this.toast.done();
        this.toast = null;
    }

    private CompletableFuture<Integer> createFfmpegCommand() {
        int width = MinecraftClient.getInstance().getWindow().getWidth();
        int height = MinecraftClient.getInstance().getWindow().getHeight();
        Path output = ClientConfigManager.CLIENT_CONFIG.recSettings.renderMode == RecordingMode.BOTH ?
                RECORD_DIR.resolve(this.filename) : RECORD_DIR;

        return CliUtils.runCommandAsync("ffmpeg", SaveFile.SHUTTER_REC_DIR.resolve(this.filename).toFile(), true,
                    FfmpegProperties.FRAMERATE.get(ClientConfigManager.CLIENT_CONFIG.recSettings.framerate.value),
                    FfmpegProperties.FORMAT,
                    FfmpegProperties.RESOLUTION.get(String.format("%dx%d", width, height)),
                    FfmpegProperties.INPUT.get(RECORD_DIR.resolve(this.filename).toAbsolutePath() + File.separator + "frame_%04d.png"),
                    FfmpegProperties.CODEC,
                    FfmpegProperties.QUALITY,
                    FfmpegProperties.PIXEL_FORMAT,
                    FfmpegProperties.OVERWRITE,
                    CommandProperty.flag(output.resolve(this.filename + ".mp4").toAbsolutePath().toString()));
    }
}
