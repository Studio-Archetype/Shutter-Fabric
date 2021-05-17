package studio.archetype.shutter.client.processing.processors;

import studio.archetype.shutter.client.processing.frames.Frame;

import java.io.Closeable;

public interface FrameProcessor<T extends Frame> extends Closeable {
    void processFrame(T frame);
}
