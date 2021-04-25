package studio.archetype.shutter;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Shutter implements ModInitializer {

    public static final String MOD_ID = "shutter";
    public static final Logger LOGGER = LogManager.getLogger();

    public static Shutter INSTANCE;

    @Override
    public void onInitialize() {
        INSTANCE = this;
    }

    public static Identifier id(String key) {
        return new Identifier(MOD_ID, key);
    }
}
