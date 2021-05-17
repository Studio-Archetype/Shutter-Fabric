package studio.archetype.shutter.client.processing.frames;

import studio.archetype.shutter.util.ScreenSize;

import java.nio.ByteBuffer;

public class OpenGlFrame implements Frame {

    private final int frameId;
    private final ScreenSize size;
    private final ByteBuffer frameData;

    public OpenGlFrame(int frameId, ScreenSize dimensions, ByteBuffer buffer) {
        this.frameId = frameId;
        this.size = dimensions;
        this.frameData = buffer;
    }

    @Override
    public int getFrameId() {
        return this.frameId;
    }

    public ScreenSize getSize() {
        return size;
    }

    public ByteBuffer getData() {
        return frameData;
    }
}
