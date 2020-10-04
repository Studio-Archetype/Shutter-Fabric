package studio.archetype.shutter;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Shutter implements ModInitializer {

    public static final String MOD_ID = "shutter";

    public static Shutter INSTANCE;

    @Override
    public void onInitialize() {
        INSTANCE = this;
    }

    public static Identifier id(String key) {
        return new Identifier(MOD_ID, key);
    }
}
