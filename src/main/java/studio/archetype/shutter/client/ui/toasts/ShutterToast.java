package studio.archetype.shutter.client.ui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public abstract class ShutterToast implements Toast {

    private static final Identifier TEXTURE = new Identifier("shutter", "textures/ui/toasts.png");

    private final int width, height;
    private final long visibilityTime;

    public ShutterToast(int width, int height, long visibilityTime) {
        this.width = width; this.height = height;
        this.visibilityTime = visibilityTime;
    }

    public ShutterToast(int width, int height) {
        this(width, height, 5000L);
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        ToastGraphics.BACKGROUND.draw(matrices, 0, 0, manager);
        drawToastContent(matrices, manager);
        return getVisibility(startTime);
    }

    protected abstract void drawToastContent(MatrixStack matrices, ToastManager manager);

    protected Visibility getVisibility(long startTime) {
        return startTime > visibilityTime ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int getWidth() { return this.width; }

    @Override
    public int getHeight() { return this.height; }

    protected enum ToastGraphics {
        BACKGROUND(0, 0, 160, 48),
        STRIP_RED(161, 0, 4, 42),
        STRIP_GREEN(166, 0, 4, 42),
        STRIP_EXCLAIM(171, 0, 4, 42),
        PROGBAR_FRAME(0, 48, 160, 12),
        PROGBAR_BAR(4, 61, 152, 5);

        private final int u, v, width, height;

        ToastGraphics(int u, int v, int width, int height) {
            this.u = u; this.v = v;
            this.width = width; this.height = height;
        }

        public void draw(MatrixStack matrix, int x, int y, ToastManager man) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            man.drawTexture(matrix, x, y, u, v, width, height);
        }
    }
}
