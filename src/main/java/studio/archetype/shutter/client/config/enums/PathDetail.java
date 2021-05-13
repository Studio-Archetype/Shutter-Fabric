package studio.archetype.shutter.client.config.enums;

import net.minecraft.client.resource.language.I18n;

public enum PathDetail {

    ULTRA_LOW(0.1, "config.shutter.pathDetail.ultra_low"),
    LOW(0.05, "config.shutter.pathDetail.low"),
    MEDIUM(0.01, "config.shutter.pathDetail.medium"),
    HIGH(0.005, "config.shutter.pathDetail.high"),
    ABSURD(0.001, "config.shutter.pathDetail.absurd");

    public final double detail;
    private final String key;

    PathDetail(double detail, String key) {
        this.detail = detail;
        this.key = key;
    }

    public String toString() {
        return I18n.translate(this.key);
    }
}
