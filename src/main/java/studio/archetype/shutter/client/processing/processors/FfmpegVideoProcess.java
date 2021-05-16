package studio.archetype.shutter.client.processing.processors;

import net.minecraft.client.MinecraftClient;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.config.FfmpegRecordConfig;
import studio.archetype.shutter.client.config.SaveFile;
import studio.archetype.shutter.client.processing.CommandProperty;
import studio.archetype.shutter.client.processing.FfmpegProperties;
import studio.archetype.shutter.util.CliUtils;

import java.io.IOException;
import java.io.OutputStream;

public class FfmpegVideoProcess implements FrameProcessor {

    private static final String CMD = "ffmpeg";

    private final Process ffmpegProcess;
    private final OutputStream toFfmpeg;
    //private final InputStream fromFfmpeg;

    private final String filename;

    public FfmpegVideoProcess(String filename) {
        this.filename = filename;

        Process temp = null;
        try {
            temp = CliUtils.createCommandProcess(
                    CMD,
                    SaveFile.SHUTTER_REC_DIR.resolve(filename).toFile(),
                    true,
                    createCommandProperties());
        } catch(IOException ex) {
            //TODO Catch Errors
        }
        this.ffmpegProcess = temp;
        this.toFfmpeg = this.ffmpegProcess.getOutputStream();

    }

    @Override
    public void process() {

    }

    @Override
    public void close() throws IOException {

    }

    private CommandProperty[] createCommandProperties() {
        FfmpegRecordConfig config = ClientConfigManager.FFMPEG_CONFIG;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getWidth();
        int height = client.getWindow().getHeight();

        return new CommandProperty[] {
                FfmpegProperties.OVERWRITE,

                FfmpegProperties.RESOLUTION.get(String.format("%dx%d", width, height)),
                FfmpegProperties.FRAMERATE.get(config.framerate.value),
                FfmpegProperties.FORMAT.get("rawvideo"),
                FfmpegProperties.PIXEL_FORMAT.get("argb"),
                FfmpegProperties.INPUT,

                FfmpegProperties.CODEC.get(config.codec.value),
                FfmpegProperties.QUALITY,

                FfmpegProperties.OUTPUT.get(filename + ".mov")};
    }
}
