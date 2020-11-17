package studio.archetype.shutter.client.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigManager;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.ActionResult;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.CameraPathManager;

public class ClientConfigManager {

    public static ClientConfig CLIENT_CONFIG;

    public static void register() {
        AutoConfig.register(ClientConfig.class, JanksonConfigSerializer::new);
        CLIENT_CONFIG = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
        AutoConfig.getConfigHolder(ClientConfig.class).registerSaveListener((h, c) -> {
            ShutterClient shutter = ShutterClient.INSTANCE;
            if(shutter.getPathFollower().isFollowing())
                shutter.getPathFollower().end();

            CameraPathManager manager = shutter.getPathManager(MinecraftClient.getInstance().world);
            CameraPath path = manager.getPath(CameraPathManager.DEFAULT_PATH);
            path.calculatePath();

            return ActionResult.PASS;
        });
    }
}
