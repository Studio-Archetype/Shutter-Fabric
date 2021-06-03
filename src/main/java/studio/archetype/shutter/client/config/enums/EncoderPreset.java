package studio.archetype.shutter.client.config.enums;

import net.minecraft.client.resource.language.I18n;

public enum EncoderPreset {

    ULTRA_FAST("ultrafast"),
    SUPER_FAST("superfast"),
    VERY_FAST("veryfast"),
    FASTER("faster"),
    FAST("fast"),
    MEDIUM("medium"),
    SLOW("slow"),
    SLOWER("slower"),
    VERY_SLOW("veryslow");

    public String value;

    EncoderPreset(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return I18n.translate("config.shutter.recording.preset." + this.value);
    }
}
