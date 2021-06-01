package studio.archetype.shutter.client.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gl.Framebuffer;

public interface WorldRenderedCallback {

    Event<WorldRenderedCallback> EVENT = EventFactory.createArrayBacked(WorldRenderedCallback.class, listeners -> (buffer) -> {
        for(WorldRenderedCallback c : listeners)
            c.onRendered(buffer);
    });

    void onRendered(Framebuffer buffer);
}
