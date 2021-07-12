package studio.archetype.shutter.client.processing.capturing;

import studio.archetype.shutter.client.processing.frames.Frame;

import java.io.Closeable;

public interface FrameCapturer<T extends Frame> extends Closeable {

    boolean isDone();

    T capture();
}
