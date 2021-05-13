package studio.archetype.shutter.client.config.enums;

import net.minecraft.client.resource.language.I18n;

public enum NotificationTarget {

    CHAT("config.shutter.notification.chat"),
    ACTION_BAR("config.shutter.notification.action_bar"),
    TOAST("config.shutter.notification.toasts");

    private final String key;

    NotificationTarget(String key) {
        this.key = key;
    }

    public String toString() {
        return I18n.translate(this.key);
    }
}
