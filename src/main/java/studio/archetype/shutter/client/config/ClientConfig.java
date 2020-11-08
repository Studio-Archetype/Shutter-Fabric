package studio.archetype.shutter.client.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.resource.language.I18n;
import studio.archetype.shutter.Shutter;

@Config(name = Shutter.MOD_ID)
public class ClientConfig implements ConfigData {

    public float curveDetail = 0.1F;
    public int pathTime = 100;

    @ConfigEntry.Gui.CollapsibleObject
    public final CameraPathSettings pathSettings = new CameraPathSettings();

    public static class CameraPathSettings {

        @ConfigEntry.BoundedDiscrete(max = 100, min = 0)
        public int nodeTransparency = 50;

        public boolean showNodeDirectionalBeam = false;

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public PathStyle pathStyle = PathStyle.LINE;

        @ConfigEntry.ColorPicker
        public int pathColour = 0;
    }

    public enum PathStyle {
        LINE("config.shutter.pathStyle.line"),
        CUBES("config.shutter.pathStyle.cubes"),
        NONE("config.shutter.pathStyle.none"),
        DEBUG("config.shutter.pathStyle.debug");

        String key;

        PathStyle(String key) {
            this.key = key;
        }

        public String toString() {
            return I18n.translate(this.key);
        }
    }
}
