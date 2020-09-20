package studio.archetype.shutter.client;

import net.fabricmc.api.ClientModInitializer;
import studio.archetype.shutter.client.entityrenderer.EntityRenderers;

public class ShutterClient implements ClientModInitializer {

    public static ShutterClient INSTANCE;

    private InputHandler inputHandler;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        EntityRenderers.register();
        this.inputHandler = new InputHandler();
    }
}
