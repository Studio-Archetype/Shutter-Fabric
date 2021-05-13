package studio.archetype.shutter.client.config.enums;

import net.minecraft.client.resource.language.I18n;

public enum PathStyle {

    LINE("config.shutter.pathStyle.line"),
    CUBES("config.shutter.pathStyle.cubes"),
    ADVANCED("config.shutter.pathStyle.debug");

    private final String key;

    PathStyle(String key) {
        this.key = key;
    }

    public String toString() {
        return I18n.translate(this.key);
    }
}
