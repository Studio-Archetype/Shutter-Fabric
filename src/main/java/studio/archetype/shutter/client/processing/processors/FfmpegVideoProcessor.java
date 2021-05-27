package studio.archetype.shutter.client.processing.processors;

import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBImageWrite;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.config.FfmpegRecordConfig;
import studio.archetype.shutter.client.config.SaveFile;
import studio.archetype.shutter.client.config.enums.RecordingMode;
import studio.archetype.shutter.util.cli.CommandProperty;
import studio.archetype.shutter.util.cli.FfmpegProperties;
import studio.archetype.shutter.client.processing.frames.RgbaFrame;
import studio.archetype.shutter.util.ByteBufferPool;
import studio.archetype.shutter.util.cli.CliUtils;
import studio.archetype.shutter.util.ScreenSize;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class FfmpegVideoProcessor implements FrameProcessor<RgbaFrame> {

    private static final String CMD = "ffmpeg";

    private final String filename;
    private final ScreenSize size;
    private final File directory;
    private final boolean writeFrames, encodeVideo;
    private File frameDirectory;

    private Process ffmpegProcess;
    private OutputStream toFfmpeg;
    private WritableByteChannel dataChannel;

    public FfmpegVideoProcessor(String filename, ScreenSize size) throws IOException {
        this.filename = filename;
        this.size = size;
        this.directory = SaveFile.SHUTTER_REC_DIR.resolve(filename).toFile();
        this.directory.mkdirs();

        this.writeFrames = ClientConfigManager.CLIENT_CONFIG.recSettings.renderMode != RecordingMode.VIDEO;
        this.encodeVideo = ClientConfigManager.CLIENT_CONFIG.recSettings.renderMode != RecordingMode.FRAMES;

        if(writeFrames) {
            this.frameDirectory = new File(this.directory, "frames");
            this.frameDirectory.mkdir();
        }

        if(encodeVideo) {
            try {
                ffmpegProcess = CliUtils.createCommandProcess(CMD, this.directory, true, createCommandProperties());
            } catch(IOException ex) {
                //TODO Catch Errors
                ex.printStackTrace();
            }

            this.toFfmpeg = this.ffmpegProcess.getOutputStream();
            this.dataChannel = Channels.newChannel(this.toFfmpeg);

            new Thread(() -> {
                try (FileOutputStream out = new FileOutputStream(new File(this.directory,"ffmpeg.log"))){
                    IOUtils.copy(ffmpegProcess.getInputStream(), out);
                } catch(IOException ignored) { }
            }).start();
        }
    }

    @Override
    public void processFrame(RgbaFrame frame) {
        try {
            if(this.encodeVideo)
                this.dataChannel.write(frame.getData());
            if(this.writeFrames)
                writeFrameToImage(frame);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            ByteBufferPool.release(frame.getData());
        }
    }

    @Override
    public void close() {
        if(encodeVideo) {
            IOUtils.closeQuietly(toFfmpeg);
            ffmpegProcess.destroy();
        }
    }

    private void writeFrameToImage(RgbaFrame frame) {
        frame.getData().rewind();
        File path = new File(this.frameDirectory, String.format("frame_%d.png", frame.getFrameId()));
        STBImageWrite.stbi_write_png(path.getAbsolutePath(), frame.getSize().getWidth(), frame.getSize().getHeight(), 4, frame.getData(), 0);
    }

    private CommandProperty[] createCommandProperties() {
        FfmpegRecordConfig config = ClientConfigManager.FFMPEG_CONFIG;

        return new CommandProperty[] {
                FfmpegProperties.OVERWRITE,
                FfmpegProperties.HIDE_BANNER,

                FfmpegProperties.RESOLUTION.get(String.format("%dx%d", this.size.getWidth(), this.size.getHeight())),
                FfmpegProperties.FRAMERATE.get(config.framerate.value),
                FfmpegProperties.FORMAT.get("rawvideo"),
                FfmpegProperties.PIXEL_FORMAT.get("rgba"),
                FfmpegProperties.INPUT,

                FfmpegProperties.CODEC.get(config.codec.value),
                FfmpegProperties.QUALITY,

                FfmpegProperties.OUTPUT.get(filename + ".mp4")};
    }
}
