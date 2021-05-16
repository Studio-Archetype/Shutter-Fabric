package studio.archetype.shutter.client.processing.processors;

import java.io.Closeable;

public interface FrameProcessor extends Closeable {
    void process();
}
