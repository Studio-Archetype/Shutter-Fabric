package studio.archetype.shutter.client.config.enums;

import net.minecraft.client.resource.language.I18n;

public enum DirectionalBeamStyle {

    SHOW("config.shutter.directional.show"),
    HIDE("config.shutter.directional.hide"),
    ADVANCED("config.shutter.directional.advanced");

    private final String key;

    DirectionalBeamStyle(String key) {
        this.key = key;
    }

    public String toString() {
        return I18n.translate(this.key);
    }
}
