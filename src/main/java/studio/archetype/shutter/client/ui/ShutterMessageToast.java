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

import java.util.List;

public class ShutterMessageToast extends ShutterToast {

    private static final int GENERAL_Y_OFFSET = 4;

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
        //TODO Auto-Linebreak
        type.graphic.draw(matrices, 3, 3, manager);
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

        renderer.draw(matrices, title, 10, GENERAL_Y_OFFSET, 0xFFFFFFFF);

        List<OrderedText> lines = renderer.wrapLines(subtitle, 130);

        for(int i = 0; i < lines.size(); i++) {
            int yOffset = (renderer.fontHeight + 2) * (i + 1) + GENERAL_Y_OFFSET;
            renderer.draw(matrices, lines.get(i), 10, yOffset, 0xFFFFFFFF);
        }

        if(subsubTitle != null) {
            int offset = lines.size() * (renderer.fontHeight + 2);
            List<OrderedText> subLines = renderer.wrapLines(subsubTitle, 130);
            for(int i = 0; i < subLines.size(); i++) {
                int yOffset = (renderer.fontHeight + 2) * (i + 1) + offset + GENERAL_Y_OFFSET;
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
