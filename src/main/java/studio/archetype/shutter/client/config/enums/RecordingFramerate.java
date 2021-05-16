package studio.archetype.shutter.client.config.enums;

import net.minecraft.client.resource.language.I18n;

public enum RecordingFramerate {

    F20("config.shutter.framerate.20", 20),
    F40("config.shutter.framerate.40", 40),
    F60("config.shutter.framerate.60", 60),
    F80("config.shutter.framerate.80", 80),
    F100("config.shutter.framerate.100", 100),
    F120("config.shutter.framerate.120", 120);

    public final int value;
    private final String key;

    RecordingFramerate(String key, int framerate) {
        this.key = key;
        this.value = framerate;
    }

    public String toString() {
        return I18n.translate(this.key);
    }
}
