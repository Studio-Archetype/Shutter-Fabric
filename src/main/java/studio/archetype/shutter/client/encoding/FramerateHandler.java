package studio.archetype.shutter.client.encoding;

import net.minecraft.client.render.RenderTickCounter;

public class FramerateHandler {

    private int targetFramerate, currentFramecounter;
    private float tickDelta;
    private boolean isFrameQueued, hasTicked, hasServerTicked = false;

    public FramerateHandler() {
        this.targetFramerate = this.currentFramecounter = 0;
    }

    public void syncRenderingAndTicks(int framerate) {
        this.targetFramerate = framerate;
        this.currentFramecounter = 0;
        this.tickDelta = 1.0F / targetFramerate;
    }

    public int processTick(RenderTickCounter tickCounter, long timeMillis) {
        if(targetFramerate == 0)
            return tickCounter.beginRenderTick(timeMillis);

        tickCounter.beginRenderTick(timeMillis);

        if(!isFrameQueued) {
            tickCounter.lastFrameDuration = 1000.0F / targetFramerate;
            tickCounter.tickDelta = this.tickDelta;
        } else {
            tickCounter.tickDelta = 0;
            tickCounter.lastFrameDuration = 0;
        }

        if(currentFramecounter % (targetFramerate / 20) == 0 && !hasTicked) {
            hasTicked = true;
            return 1;
        }

        return 0;
    }

    public boolean skipRenderTick() {
        return targetFramerate != 0 && isFrameQueued;
    }

    public boolean isServerTickValid() {
        if(targetFramerate == 0)
            return true;

        if(currentFramecounter % (targetFramerate / 20) == 0 && !hasServerTicked) {
            hasServerTicked = true;
            return true;
        }

        return false;
    }

    public void updateBufferCapture() {
        if(isFrameQueued || targetFramerate == 0)
            return;

        //Do the thing to queue frame capture
        //isFrameQueued = true;
        hasTicked = hasServerTicked = false;
        currentFramecounter++;

        if(currentFramecounter >= targetFramerate)
            this.currentFramecounter = 0;
    }
}
