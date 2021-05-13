package studio.archetype.shutter.client.config.enums;

import net.minecraft.client.resource.language.I18n;

public enum RecordingMode {

    FRAMES("config.shutter.recording.frames"),
    VIDEO("config.shutter.recording.video"),
    BOTH("config.shutter.recording.both");

    private final String key;

    RecordingMode(String key) {
        this.key = key;
    }

    public String toString() {
        return I18n.translate(this.key);
    }
}
