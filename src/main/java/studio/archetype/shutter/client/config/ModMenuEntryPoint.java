package studio.archetype.shutter.client.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import studio.archetype.shutter.Shutter;

public class ModMenuEntryPoint implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return p -> {
            ConfigScreenProvider<ClientConfig> provider = (ConfigScreenProvider<ClientConfig>) AutoConfig.getConfigScreen(ClientConfig.class, p);
            provider.setOptionFunction((gen, field) -> "config." + Shutter.MOD_ID + "." + field.getName());
            return provider.get();
        };
    }
}
