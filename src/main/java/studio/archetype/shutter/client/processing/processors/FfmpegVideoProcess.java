package studio.archetype.shutter.client.processing.processors;

import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.IOUtils;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.config.FfmpegRecordConfig;
import studio.archetype.shutter.client.config.SaveFile;
import studio.archetype.shutter.client.processing.CommandProperty;
import studio.archetype.shutter.client.processing.FfmpegProperties;
import studio.archetype.shutter.client.processing.frames.BitmapFrame;
import studio.archetype.shutter.client.processing.frames.OpenGlFrame;
import studio.archetype.shutter.util.ByteBufferPool;
import studio.archetype.shutter.util.CliUtils;
import studio.archetype.shutter.util.ScreenSize;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class FfmpegVideoProcess implements FrameProcessor<BitmapFrame> {

    private static final String CMD = "ffmpeg";

    private final String filename;
    private final ScreenSize size;

    private Process ffmpegProcess;
    private OutputStream toFfmpeg;
    private WritableByteChannel dataChannel;

    public FfmpegVideoProcess(String filename, ScreenSize size) {
        this.filename = filename;
        this.size = size;
        SaveFile.SHUTTER_REC_DIR.resolve(filename).toFile().mkdirs();

        try {
            ffmpegProcess = CliUtils.createCommandProcess(
                    CMD,
                    SaveFile.SHUTTER_REC_DIR.resolve(filename).toFile(),
                    true,
                    createCommandProperties());
        } catch(IOException ex) {
            //TODO Catch Errors
            ex.printStackTrace();
        }

        this.toFfmpeg = this.ffmpegProcess.getOutputStream();
        this.dataChannel = Channels.newChannel(this.toFfmpeg);
    }

    @Override
    public void processFrame(BitmapFrame frame) {
        try {
            this.dataChannel.write(frame.getData());
            System.out.println("Exported frame");
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            ByteBufferPool.release(frame.getData());
        }
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(toFfmpeg);
    }

    private CommandProperty[] createCommandProperties() {
        FfmpegRecordConfig config = ClientConfigManager.FFMPEG_CONFIG;

        return new CommandProperty[] {
                FfmpegProperties.OVERWRITE,

                FfmpegProperties.RESOLUTION.get(String.format("%dx%d", this.size.getWidth(), this.size.getHeight())),
                FfmpegProperties.FRAMERATE.get(config.framerate.value),
                FfmpegProperties.FORMAT.get("rawvideo"),
                FfmpegProperties.PIXEL_FORMAT.get("argb"),
                FfmpegProperties.INPUT,

                FfmpegProperties.CODEC.get(config.codec.value),
                FfmpegProperties.QUALITY,

                FfmpegProperties.OUTPUT.get(filename + ".mov")};
    }
}
