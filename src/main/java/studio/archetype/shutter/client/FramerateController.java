package studio.archetype.shutter.client;

import net.minecraft.client.render.RenderTickCounter;

public final class FramerateController {

    private int targetFramerate, framesPerTick;
    private float delta, frameTime;
    private boolean advanceManually;

    private int tickFramesCounter;
    private boolean shouldTickServer, allowNextFrame;

    public void startControlling(int targetFramerate, boolean advanceManually) {
        this.targetFramerate = targetFramerate;
        this.framesPerTick = targetFramerate / 20;
        this.delta = 1.0F / targetFramerate;
        this.frameTime = 1000.0F / targetFramerate;
        this.advanceManually = advanceManually;

        this.tickFramesCounter = 0;
        this.shouldTickServer = this.allowNextFrame = false;
    }

    public void stopControlling() {
        this.targetFramerate = 0;
    }

    public boolean isControlling() {
        return targetFramerate > 0;
    }

    public void progressFrame() {
        if(advanceManually)
            this.allowNextFrame = true;
    }

    public int processTick(RenderTickCounter tickCounter, long timeMillis) {
        int passedTicks = tickCounter.beginRenderTick(timeMillis);
        if(!isControlling())
            return passedTicks;

        if(allowNextFrame) {
            tickCounter.lastFrameDuration = this.frameTime;
            tickCounter.tickDelta = this.delta;
            if(tickFramesCounter >= framesPerTick - 1) {
                this.tickFramesCounter = 0;
                this.shouldTickServer = true;
                return 1;
            } else
                tickFramesCounter++;
            this.allowNextFrame = !advanceManually;
        } else {
            tickCounter.lastFrameDuration = 0;
            tickCounter.tickDelta = 0;
        }

        return 0;
    }

    public boolean isServerTickValid() {
        if(!isControlling())
            return true;
        else if(shouldTickServer) {
            this.shouldTickServer = false;
            return true;
        } else
            return false;
    }
}
