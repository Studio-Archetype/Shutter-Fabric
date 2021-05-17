package studio.archetype.shutter.client.processing.frames;

public class DummyFrame implements Frame {

    private final int frameId;

    public DummyFrame(int frameId) {
        this.frameId = frameId;
    }

    @Override
    public int getFrameId() {
        return 0;
    }
}
