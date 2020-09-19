package studio.archetype.shutter.client;

import net.fabricmc.api.ClientModInitializer;

public class ShutterClient implements ClientModInitializer {

    public static ShutterClient INSTANCE;

    private InputHandler inputHandler;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        this.inputHandler = new InputHandler();
    }
}
