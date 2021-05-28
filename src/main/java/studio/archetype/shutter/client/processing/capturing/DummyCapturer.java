package studio.archetype.shutter.client.processing.capturing;

import studio.archetype.shutter.client.processing.frames.DummyFrame;

public class DummyCapturer implements FrameCapturer<DummyFrame> {

    @Override
    public DummyFrame capture() {
        return null;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void close() { }
}
