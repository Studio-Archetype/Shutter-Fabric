package studio.archetype.shutter.client.processing.processors;

import studio.archetype.shutter.client.processing.frames.Frame;

import java.io.Closeable;
import java.io.IOException;

public interface FrameProcessor<T extends Frame> extends Closeable {
    void processFrame(T frame) throws IOException;
}
