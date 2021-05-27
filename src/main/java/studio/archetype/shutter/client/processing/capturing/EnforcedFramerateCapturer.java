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

    public EnforcedFramerateCapturer(int targetFrameCount, ScreenSize size) {
        this.targetFrameCount = targetFrameCount;
        this.size = size;
        this.fb = MinecraftClient.getInstance().getFramebuffer();
        ShutterClient.INSTANCE.getFramerateController().startControlling(ClientConfigManager.CLIENT_CONFIG.recSettings.framerate.value);
        this.framesDone = 0;
        System.out.println(targetFrameCount);
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
        ShutterClient.INSTANCE.getFramerateController().allowNextFrame();
        return new OpenGlFrame(framesDone++, size, buffer);
    }

    @Override
    public void close() {
        ShutterClient.INSTANCE.getFramerateController().stopControlling();
    }
}
