package studio.archetype.shutter.client.ui;

import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ShutterMessageToast extends ShutterToast {

    private Type type;
    private Text title, subtitle1, subtitle2;

    public ShutterMessageToast(Type type, Text title, Text subtitle1, Text subtitle2) {
        super(160, 48);
        this.type = type;
        this.title = title;
        this.subtitle1 = subtitle1;
        this.subtitle2 = subtitle2;
    }

    @Override
    protected void drawToastContent(MatrixStack matrices, ToastManager manager) {
        type.graphic.draw(matrices, 3, 3, manager);
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
