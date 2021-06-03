package studio.archetype.shutter.client.ui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Predicate;

public class PredicateTextboxWidget extends TextFieldWidget {

    private static final int ERROR_COLOUR = 16733525;
    private static final int VALID_COLOUR = 14737632;

    private Predicate<String> validPredicate;
    private Text label;
    private boolean isValid;

    public PredicateTextboxWidget(int x, int y, int width, int height, Text label) {
        super(MinecraftClient.getInstance().textRenderer, x, y, width, height, new LiteralText("PredicateTextField"));
        this.label = label;
    }

    public void setValidPredicate(Predicate<String> validPredicate) {
        this.validPredicate = validPredicate;
    }

    public void setLabel(Text label) {
        this.label = label;
    }

    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public void tick() {
        if((isValid = validPredicate.test(this.getText())))
            setEditableColor(VALID_COLOUR);
        else
            setEditableColor(ERROR_COLOUR);
        super.tick();
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        if(label != null) {
            TextRenderer text = MinecraftClient.getInstance().textRenderer;
            text.drawWithShadow(matrices, label, this.x, this.y - text.fontHeight - 1, this.isValid ? VALID_COLOUR : ERROR_COLOUR);
        }
    }
}
