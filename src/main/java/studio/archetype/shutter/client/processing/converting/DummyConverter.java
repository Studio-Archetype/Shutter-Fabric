package studio.archetype.shutter.client.processing.converting;

import studio.archetype.shutter.client.processing.frames.DummyFrame;

public class DummyConverter implements FrameConverter<DummyFrame, DummyFrame> {

    @Override
    public DummyFrame convert(DummyFrame inputFrame) {
        return inputFrame;
    }
}
