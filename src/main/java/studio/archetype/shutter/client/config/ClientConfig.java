package studio.archetype.shutter.client.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import me.shedaniel.fiber2cloth.api.ClothSetting;
import net.minecraft.client.resource.language.I18n;

@Settings
@ClothSetting
public class ClientConfig {

    @Setting.Group
    @ClothSetting.CollapsibleObject
    @ClothSetting.Tooltip("config.shutter.tooltip.pathSettings")
    public final CameraPathSettings pathSettings = new CameraPathSettings();

    public static class CameraPathSettings {

        @ClothSetting.Slider
        @ClothSetting.Tooltip("config.shutter.tooltip.nodeTransparency")
        @Setting.Constrain.Range(min = 0, max = 100, step = 1)
        public int nodeTransparency = 50;

        @ClothSetting.Tooltip("config.shutter.tooltip.showDirectionalBeam")
        public boolean showNodeDirectionalBeam = false;

        @ClothSetting.EnumHandler(ClothSetting.EnumHandler.EnumDisplayOption.DROPDOWN)
        @ClothSetting.Tooltip("config.shutter.tooltip.pathStyle")
        public PathStyle pathStyle = PathStyle.LINE;
    }

    public enum PathStyle {
        LINE("config.shutter.pathStyle.line"),
        CUBES("config.shutter.pathStyle.cubes"),
        NONE("config.shutter.pathStyle.none");

        String key;

        PathStyle(String key) {
            this.key = key;
        }

        public String toString() {
            return I18n.translate(this.key);
        }
    }
}
