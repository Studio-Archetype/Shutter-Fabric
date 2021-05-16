package studio.archetype.shutter.client.config.enums;

import net.minecraft.client.resource.language.I18n;

public enum RecordingCodec {

    Hx264("config.shutter.recording.hx264", "libx264"),
    Hx265("config.shutter.recording.hx265", "libx265");

    public final String value;
    private final String key;

    RecordingCodec(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return I18n.translate(this.key);
    }
}
