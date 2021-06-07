package studio.archetype.shutter.client.processing.converting;

import studio.archetype.shutter.client.processing.frames.OpenGlFrame;
import studio.archetype.shutter.client.processing.frames.RgbaFrame;
import studio.archetype.shutter.client.util.ScreenSize;

import java.nio.ByteBuffer;

public class OpenGl2BitmapConverter implements FrameConverter<OpenGlFrame, RgbaFrame> {

    private byte[] row, rowSwap;

    @Override
    public RgbaFrame convert(OpenGlFrame rawFrame) {
        ScreenSize size = rawFrame.getSize();
        int rowSize = size.getWidth() * 4;
        if (row == null || row.length < rowSize) {
            row = new byte[rowSize];
            rowSwap = new byte[rowSize];
        }
        ByteBuffer buffer = rawFrame.getData();
        int rows = size.getHeight();
        byte[] row = this.row;
        byte[] rowSwap = this.rowSwap;
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
        return new RgbaFrame(rawFrame.getFrameId(), size, buffer);
    }
}
