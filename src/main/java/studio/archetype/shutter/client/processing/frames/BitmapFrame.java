package studio.archetype.shutter.client.processing.frames;

import studio.archetype.shutter.util.ScreenSize;

import java.nio.ByteBuffer;

public class BitmapFrame implements Frame {

    private final int frameId;
    private final ScreenSize size;
    private final ByteBuffer frameData;

    public BitmapFrame(int frameId, ScreenSize dimensions, ByteBuffer buffer) {
        this.frameId = frameId;
        this.size = dimensions;
        this.frameData = buffer;
    }

    public static BitmapFrame fromOgl(OpenGlFrame frame) {
        ScreenSize size = frame.getSize();
        int rowSize = size.getWidth() * 4;
        ByteBuffer buffer = frame.getData();
        int rows = size.getHeight();
        byte[] row = new byte[rowSize];
        byte[] rowSwap = new byte[rowSize];

        for (int i = 0; i < rows / 2; i++) {
            int from = rowSize * i;
            int to = rowSize * (rows - i - 1);
            buffer.position(from);
            buffer.get(row);
            buffer.position(to);
            buffer.get(rowSwap);
            buffer.position(to);
            buffer.put(row);
            buffer.position(from);
            buffer.put(rowSwap);
        }
        buffer.rewind();
        return new BitmapFrame(frame.getFrameId(), size, buffer);
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
