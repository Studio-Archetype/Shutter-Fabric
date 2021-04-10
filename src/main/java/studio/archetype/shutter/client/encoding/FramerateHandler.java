package studio.archetype.shutter.client.encoding;

import net.minecraft.client.render.RenderTickCounter;

public class FramerateHandler {

    private int targetFramerate, currentFramecounter;
    private int savedTicks;
    private float savedDelta;

    public FramerateHandler() {
        this.targetFramerate = this.currentFramecounter = 0;
    }

    public void syncRenderingAndTicks(int framerate) {
        this.targetFramerate = framerate / 20;
        this.currentFramecounter = 0;
    }

    public int modifyTick(RenderTickCounter renderTickCounter, long timeMillis) {
        if(targetFramerate == 0)
            return renderTickCounter.beginRenderTick(timeMillis);

        if(currentFramecounter == 0) {
            this.savedTicks = renderTickCounter.beginRenderTick(timeMillis);
            this.savedDelta = renderTickCounter.tickDelta;
        }

        if(currentFramecounter < targetFramerate) {
            currentFramecounter++;
            renderTickCounter.tickDelta = 0;
            return 0;
        } else {
            currentFramecounter = 0;
            renderTickCounter.beginRenderTick(timeMillis);
            renderTickCounter.tickDelta = savedDelta;
            return savedTicks;
        }
    }
}
