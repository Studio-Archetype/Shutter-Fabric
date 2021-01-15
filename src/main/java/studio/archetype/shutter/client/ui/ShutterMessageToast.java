package studio.archetype.shutter.client.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.config.Order;

import java.util.List;

public class ShutterMessageToast extends ShutterToast {

    private static final int TOAST_HEIGHT = 48;
    private static final int WRAP_COUNT = 130;

    private final Type type;
    private final Text title, subtitle, subsubTitle;

    public ShutterMessageToast(Type type, Text title, Text subtitle, Text subsubTitle) {
        super(160, 48);
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.subsubTitle = subsubTitle;
    }

    @Override
    protected void drawToastContent(MatrixStack matrices, ToastManager manager) {
        type.graphic.draw(matrices, 3, 3, manager);
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

        List<OrderedText> lines = renderer.wrapLines(subtitle, WRAP_COUNT);
        int totalHeight = (lines.size() + 1) * (renderer.fontHeight + 2) - 2;
        List<OrderedText> subLines = null;
        if(subsubTitle != null) {
            subLines = renderer.wrapLines(subsubTitle, WRAP_COUNT);
            totalHeight += subLines.size() * (renderer.fontHeight + 2);
        }

        int yStart = (TOAST_HEIGHT - totalHeight) / 2;

        renderer.draw(matrices, title, 10, yStart, 0xFFFFFFFF);

        for(int i = 0; i < lines.size(); i++) {
            int yOffset = (renderer.fontHeight + 2) * (i + 1) + yStart;
            renderer.draw(matrices, lines.get(i), 10, yOffset, 0xFFFFFFFF);
        }

        if(subLines != null) {
            int offset = lines.size() * (renderer.fontHeight + 2);
            for(int i = 0; i < subLines.size(); i++) {
                int yOffset = (renderer.fontHeight + 2) * (i + 1) + offset + yStart;
                renderer.draw(matrices, subLines.get(i), 10, yOffset, 0xFFFFFFFF);

            }
        }
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
