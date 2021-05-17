package studio.archetype.shutter.client.processing.converting;

import studio.archetype.shutter.client.processing.frames.Frame;

public interface FrameConverter<I extends Frame, O extends Frame> {
    O convert(I inputFrame);
}
