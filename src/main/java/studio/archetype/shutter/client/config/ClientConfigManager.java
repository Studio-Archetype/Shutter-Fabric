package studio.archetype.shutter.client.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.gui.ConfigScreenProvider;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import studio.archetype.shutter.Shutter;

public class ClientConfigManager implements ModMenuApi {

    public static ClientConfig CLIENT_CONFIG;

    public static void register() {
        AutoConfig.register(ClientConfig.class, JanksonConfigSerializer::new);
        CLIENT_CONFIG = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return p -> {
            ConfigScreenProvider<ClientConfig> provider = (ConfigScreenProvider<ClientConfig>) AutoConfig.getConfigScreen(ClientConfig.class, p);
            provider.setOptionFunction((gen, field) -> "config." + Shutter.MOD_ID + "." + field.getName());
            return provider.get();
        };
    }
}
