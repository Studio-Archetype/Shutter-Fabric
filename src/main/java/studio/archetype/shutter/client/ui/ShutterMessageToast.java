package studio.archetype.shutter.client.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ShutterMessageToast extends ShutterToast {

    private final Type type;
    private final Text title, subtitle1;

    public ShutterMessageToast(Type type, Text title, Text subtitle1) {
        super(160, 48);
        this.type = type;
        this.title = title;
        this.subtitle1 = subtitle1;
    }

    @Override
    protected void drawToastContent(MatrixStack matrices, ToastManager manager) {
        type.graphic.draw(matrices, 3, 3, manager);
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        renderer.draw(matrices, title, 10, 13, 0xFFFFFFFF);
        renderer.draw(matrices, subtitle1, 10, 13 + renderer.fontHeight + 2, 0xFFFFFFFF);
    }

    public enum Type {
        POSITIVE(ToastGraphics.STRIP_GREEN),
        NEGATIVE(ToastGraphics.STRIP_RED),
        ERROR(ToastGraphics.STRIP_EXCLAIM);

        public ToastGraphics graphic;

        Type(ToastGraphics graphic) {
            this.graphic = graphic;
        }
    }
}
