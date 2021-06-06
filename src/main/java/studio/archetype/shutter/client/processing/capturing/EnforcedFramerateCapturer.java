package studio.archetype.shutter.client.processing.capturing;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.processing.frames.OpenGlFrame;
import studio.archetype.shutter.util.ByteBufferPool;
import studio.archetype.shutter.util.ScreenSize;

import java.nio.ByteBuffer;

public class EnforcedFramerateCapturer implements FrameCapturer<OpenGlFrame> {

    private final int targetFrameCount;
    private final ScreenSize size;
    private final Framebuffer fb;

    private int framesDone;

    public EnforcedFramerateCapturer(int fps, ScreenSize size) {
        this.targetFrameCount = fps * (ClientConfigManager.FFMPEG_CONFIG.pathTimeTicks / 20);
        this.size = size;
        this.fb = MinecraftClient.getInstance().getFramebuffer();
        ShutterClient.INSTANCE.getFramerateController().startControlling(ClientConfigManager.FFMPEG_CONFIG.framerate.value, true);
        this.framesDone = 0;
    }

    @Override
    public boolean isDone() {
        return this.framesDone >= this.targetFrameCount;
    }

    @Override
    public OpenGlFrame capture() {
        ByteBuffer buffer = ByteBufferPool.allocate(size.getWidth() * size.getHeight() * 4);
        fb.beginWrite(true);
        GL11.glReadPixels(0, 0, size.getWidth(), size.getHeight(), GL12.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        fb.endWrite();
        buffer.rewind();
        ShutterClient.INSTANCE.getFramerateController().progressFrame();
        return new OpenGlFrame(framesDone++, size, buffer);
    }

    @Override
    public void close() {
        ShutterClient.INSTANCE.getFramerateController().stopControlling();
    }
}
