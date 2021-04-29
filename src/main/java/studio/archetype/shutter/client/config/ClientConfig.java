package studio.archetype.shutter.client.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.minecraft.client.resource.language.I18n;
import studio.archetype.shutter.Shutter;

@Config(name = Shutter.MOD_ID)
public class ClientConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public NotificationTarget notificationTarget = NotificationTarget.TOAST;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public RecordingSettings recSettings = new RecordingSettings();

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public final PathGenerationSettings genSettings = new PathGenerationSettings();

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject
    public final CameraPathSettings pathSettings = new CameraPathSettings();

    public static class CameraPathSettings {

        @ConfigEntry.Gui.Tooltip
        public boolean showNodeHead = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(max = 100, min = 0)
        public int nodeTransparency = 50;

        @ConfigEntry.Gui.Tooltip
        public boolean showDirectionalBeam = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public PathStyle pathStyle = PathStyle.LINE;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker
        public int pathColour = 0;
    }

    public static class PathGenerationSettings {

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public PathDetail curveDetail = PathDetail.LOW;

        @ConfigEntry.Gui.Tooltip
        public double pathTime = 100;

        @ConfigEntry.Gui.Tooltip
        public boolean hideUi = false;

        @ConfigEntry.Gui.Tooltip
        public boolean hideArmorStands = true;

        @ConfigEntry.Gui.Tooltip
        public boolean hideEntityLabels = false;
    }

    public static class RecordingSettings {

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public RecordingMode renderMode = RecordingMode.VIDEO;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        public RecordingFramerate framerate = RecordingFramerate.F60;

        @ConfigEntry.Gui.Tooltip
        public boolean skipCountdown = false;
    }

    public enum PathStyle {
        LINE("config.shutter.pathStyle.line"),
        CUBES("config.shutter.pathStyle.cubes"),
        ADVANCED("config.shutter.pathStyle.debug");

        String key;

        PathStyle(String key) {
            this.key = key;
        }

        public String toString() {
            return I18n.translate(this.key);
        }
    }

    public enum PathDetail {
        ULTRA_LOW(0.1, "config.shutter.pathDetail.ultra_low"),
        LOW(0.05, "config.shutter.pathDetail.low"),
        MEDIUM(0.01, "config.shutter.pathDetail.medium"),
        HIGH(0.005, "config.shutter.pathDetail.high"),
        ABSURD(0.001, "config.shutter.pathDetail.absurd");

        public double detail;
        String key;

        PathDetail(double detail, String key) {
            this.detail = detail;
            this.key = key;
        }

        public String toString() {
            return I18n.translate(this.key);
        }
    }

    public enum NotificationTarget {
        CHAT("config.shutter.notification.chat"),
        ACTION_BAR("config.shutter.notification.action_bar"),
        TOAST("config.shutter.notification.toasts");

        String key;

        NotificationTarget(String key) {
            this.key = key;
        }

        public String toString() {
            return I18n.translate(this.key);
        }
    }

    public enum RecordingFramerate {
        F20("config.shutter.framerate.20", 20),
        F40("config.shutter.framerate.40", 40),
        F60("config.shutter.framerate.60", 60),
        F80("config.shutter.framerate.80", 80),
        F100("config.shutter.framerate.100", 100),
        F120("config.shutter.framerate.120", 120);

        public int framerate;
        String key;

        RecordingFramerate(String key, int framerate) {
            this.key = key;
            this.framerate = framerate;
        }

        public String toString() {
            return I18n.translate(this.key);
        }
    }

    public enum RecordingMode {
        FRAMES("config.shutter.recording.frames"),
        VIDEO("config.shutter.recording.video"),
        BOTH("config.shutter.recording.both");

        String key;

        RecordingMode(String key) {
            this.key = key;
        }

        public String toString() {
            return I18n.translate(this.key);
        }
    }
}
