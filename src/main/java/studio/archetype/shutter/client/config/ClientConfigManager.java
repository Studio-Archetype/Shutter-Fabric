package studio.archetype.shutter.client.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.fiber2cloth.api.DefaultTypes;
import me.shedaniel.fiber2cloth.api.Fiber2Cloth;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import studio.archetype.shutter.Shutter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientConfigManager implements ModMenuApi {

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("shutter.json5");
    private static final JanksonValueSerializer VALUE_SERIALIZER = new JanksonValueSerializer(false);
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();
    private static final ConfigBranch CONFIG_BRANCH = ConfigTree.builder()
            .applyFromPojo(CLIENT_CONFIG, AnnotatedSettings.builder()
                    .registerTypeMapping(Identifier.class, DefaultTypes.IDENTIFIER_TYPE)
                    .apply(Fiber2Cloth::configure)
                    .build())
            .build();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> Fiber2Cloth.create(
                parent,
                Shutter.MOD_ID, CONFIG_BRANCH,
                new TranslatableText("config.shutter.title"))
                .setSaveRunnable(ClientConfigManager::saveConfig)
                .build().getScreen();
    }

    public static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try(OutputStream out = Files.newOutputStream(CONFIG_PATH)) {
                FiberSerialization.serialize(CONFIG_BRANCH, out, VALUE_SERIALIZER);
            }
        } catch(IOException e) {
            LogManager.getLogger().error("Shit's fucked!", e);
        }
    }

    public static void loadConfig() {
        if(!Files.exists(CONFIG_PATH))
            saveConfig();

        try(InputStream in = Files.newInputStream(CONFIG_PATH)) {
            FiberSerialization.deserialize(CONFIG_BRANCH, in, VALUE_SERIALIZER);
        } catch(IOException | ValueDeserializationException e) {
            LogManager.getLogger().error("Shit's fucked!", e);
        }
    }
}
