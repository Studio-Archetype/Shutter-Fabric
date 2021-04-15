package studio.archetype.shutter.client.encoding;

import net.minecraft.client.render.RenderTickCounter;

public class FramerateHandler {

    private int targetFramerate, currentFramecounter;
    private int savedTicks;
    private float savedDelta;
    private boolean isFrameQueued, hasTicked = false;

    public FramerateHandler() {
        this.targetFramerate = this.currentFramecounter = 0;
    }

    public void syncRenderingAndTicks(int framerate) {
        this.targetFramerate = framerate;
        this.currentFramecounter = 0;
    }

    public void updateTickTracker(RenderTickCounter renderTickCounter, long timeMillis) {
        if(!isFrameQueued) {
            this.savedTicks = renderTickCounter.beginRenderTick(timeMillis);
            this.savedDelta = renderTickCounter.tickDelta;
        }
    }

    public int processTick(RenderTickCounter tickCounter, long timeMillis) {
        if(targetFramerate == 0)
            return tickCounter.beginRenderTick(timeMillis);

        if(currentFramecounter % (targetFramerate / 20) == 0 && !hasTicked) {
            hasTicked = true;
            return 1;
        }

        return 0;
    }

    public boolean shouldSkipRender() {
        return targetFramerate != 0 && isFrameQueued;
    }
}
