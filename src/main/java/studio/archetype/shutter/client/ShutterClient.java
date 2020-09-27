package studio.archetype.shutter.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.decoration.ArmorStandEntity;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.entityrenderer.EntityRenderers;

public class ShutterClient implements ClientModInitializer {

    public static ShutterClient INSTANCE;

    private InputHandler inputHandler;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        ClientConfigManager.loadConfig();
        EntityRenderers.register();
        ClientNetworkHandler.register();
        this.inputHandler = new InputHandler();
    }
}
