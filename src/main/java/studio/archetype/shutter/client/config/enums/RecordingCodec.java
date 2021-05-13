package studio.archetype.shutter.client.config.enums;

import net.minecraft.client.resource.language.I18n;
import studio.archetype.shutter.client.encoding.CommandProperty;
import studio.archetype.shutter.client.encoding.FfmpegProperties;

public enum RecordingCodec {

    Hx264("config.shutter.recording.hx264", FfmpegProperties.CODEC.get("libx264")),
    Hx265("config.shutter.recording.hx265", FfmpegProperties.CODEC.get("libx265"));

    public final CommandProperty ffmpegProperty;
    private final String key;

    RecordingCodec(String key, CommandProperty property) {
        this.key = key;
        this.ffmpegProperty = property;
    }

    @Override
    public String toString() {
        return I18n.translate(this.key);
    }
}
