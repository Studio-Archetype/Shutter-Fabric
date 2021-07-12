package studio.archetype.shutter.client.processing.processors;

import studio.archetype.shutter.client.processing.frames.DummyFrame;

public class DummyProcessor implements FrameProcessor<DummyFrame> {

    @Override
    public void processFrame(DummyFrame frame) {
    }

    @Override
    public void close() {
    }
}
