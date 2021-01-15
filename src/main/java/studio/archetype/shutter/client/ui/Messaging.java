package studio.archetype.shutter.client.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import studio.archetype.shutter.client.config.ClientConfigManager;

public class Messaging {


    public static void sendMessage(MutableText headline, MutableText text, MutableText text2, MessageType type) {
        switch(ClientConfigManager.CLIENT_CONFIG.notificationTarget) {
            case CHAT:
                MutableText t = text2 == null ? text : text.append(text2);
                MinecraftClient.getInstance().player.sendMessage(formatText(t, type), false);
                break;
            case ACTION_BAR:
                t = text2 == null ? text : text.append(text2);
                MinecraftClient.getInstance().player.sendMessage(formatText(t, type), true);
                break;
            case TOAST:
                MinecraftClient.getInstance().getToastManager().add(new ShutterMessageToast(type.toastGraphic, headline, text, text2));
        }
    }

    public static void sendMessage(MutableText headline, MutableText text, MessageType type) {
        sendMessage(headline, text, null, type);
    }


        private static MutableText formatText(MutableText text, MessageType type) {
        switch(type) {
            case NEGATIVE:
                return Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.RED));
            case POSITIVE:
                return Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GREEN));
            default:
                return text;
        }
    }

    public enum MessageType {
        POSITIVE(ShutterMessageToast.Type.POSITIVE),
        NEUTRAL(ShutterMessageToast.Type.NEGATIVE),
        NEGATIVE(ShutterMessageToast.Type.ERROR);

        public ShutterMessageToast.Type toastGraphic;

        MessageType(ShutterMessageToast.Type type) {
            this.toastGraphic = type;
        }
    }
}
