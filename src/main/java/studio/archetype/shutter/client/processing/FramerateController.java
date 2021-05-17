package studio.archetype.shutter.client.processing;

import net.minecraft.client.render.RenderTickCounter;

public final class FramerateController {

    private int targetFramerate, tickFramesPassed, tickFrameThreshold = 0;
    private boolean allowNextFrame, hasTicked;

    private float tickDelta;

    public void startControlling(int framerate) {
        this.targetFramerate = framerate;
        this.tickDelta = 1.0F / framerate;
        this.tickFrameThreshold = targetFramerate / 20;
        this.allowNextFrame = this.hasTicked = true;
        this.tickFramesPassed = 0;
    }

    public void stopControlling() {
        this.targetFramerate = 0;
    }

    public boolean isControlling() {
        return targetFramerate > 0;
    }

    public void allowNextFrame() {
        this.allowNextFrame = true;
    }

    public int processTick(RenderTickCounter tickCounter, long timeMillis) {
        if(!isControlling())
            return tickCounter.beginRenderTick(timeMillis);

        tickCounter.beginRenderTick(timeMillis);

        if(allowNextFrame) {
            tickCounter.lastFrameDuration = 1000.0F / targetFramerate;
            tickCounter.tickDelta = this.tickDelta;
        } else {
            tickCounter.tickDelta = 0;
            tickCounter.lastFrameDuration = 0;
        }

        if(tickFramesPassed++ % tickFrameThreshold - 1 == 0 && !hasTicked) {
            hasTicked = true;
            return 1;
        }

        return 0;
    }

    public boolean skipRenderTick() {
        return isControlling() && !allowNextFrame;
    }

    public void finalizeTick() {
        if(!isControlling())
            return;

        this.allowNextFrame = this.hasTicked = false;
        if(tickFramesPassed >= tickFrameThreshold)
            tickFramesPassed = 0;
    }
}
