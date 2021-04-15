package studio.archetype.shutter.client.encoding;

import com.github.kokorin.jaffree.ffmpeg.Frame;
import com.github.kokorin.jaffree.ffmpeg.FrameProducer;
import com.github.kokorin.jaffree.ffmpeg.Stream;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;

public class PathFrameProducer implements FrameProducer {

    private final long fps;
    private final int width, height;

    private boolean running;
    private long nextVideoTimecode;

    public PathFrameProducer(long fps, int width, int height) {
        this.fps = fps;
        this.width = width;
        this.height = height;
        this.running = true;
        this.nextVideoTimecode = 0;
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public List<Stream> produceStreams() {
        return Collections.singletonList(new Stream()
                .setType(Stream.Type.VIDEO)
                .setId(0)
                .setTimebase(fps)
                .setWidth(width)
                .setHeight(height));
    }

    @Override
    public Frame produce() {
        if(!running)
            return null;

        Frame frame = new Frame(0, nextVideoTimecode, bufferToImage(MinecraftClient.getInstance().getFramebuffer()));
        this.nextVideoTimecode++;

        return frame;
    }

    private BufferedImage bufferToImage(Framebuffer buffer) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        image.setRGB(0, 0, width, height, getBufferColor(buffer), 0, 4);
        return image;
    }

    private int[] getBufferColor(Framebuffer buffer) {
        int size = width * height;
        long pointer = MemoryUtil.nmemAlloc((long)size * 4);

        if(RenderSystem.isOnRenderThread()) {
            RenderSystem.bindTexture(buffer.getColorAttachment());
            GlStateManager.getTexImage(3553, 0, NativeImage.Format.ABGR.getChannelCount(), 5121, pointer);
        } else {
            RenderSystem.recordRenderCall(() -> {
                RenderSystem.bindTexture(buffer.getColorAttachment());
                GlStateManager.getTexImage(3553, 0, NativeImage.Format.ABGR.getChannelCount(), 5121, pointer);
            });
        }

        return MemoryUtil.memIntBuffer(pointer, size).array();
    }
}