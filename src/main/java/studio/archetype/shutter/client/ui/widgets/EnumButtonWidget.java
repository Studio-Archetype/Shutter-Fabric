package studio.archetype.shutter.client.ui.widgets;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;

import java.util.function.Consumer;

public class EnumButtonWidget<E extends Enum<E>> extends ButtonWidget {

    private final MutableText label;
    private final E[] values;
    private final Consumer<E> action;
    private E currentValue;
    private boolean prefix = true;

    public EnumButtonWidget(MutableText label, int x, int y, int width, int height, E startValue, Consumer<E> action) {
        super(x, y, width, height, new LiteralText(startValue.toString()), null);
        this.label = label;
        this.values = startValue.getDeclaringClass().getEnumConstants();
        this.action = action;
        this.currentValue = startValue;
        setText();
    }

    public void setPrefix(boolean prefix) {
        this.prefix = prefix;
        setText();
    }

    @Override
    public void onPress() {
        this.currentValue = nextValue();
        setText();
        action.accept(this.currentValue);
    }

    private void setText() {
        if(prefix)
            setMessage(label.append(": " + this.currentValue.toString()));
        else
            setMessage(new LiteralText(this.currentValue.toString()));
    }

    private E nextValue() {
        for(int i = 0; i < values.length; i++) {
            if (values[i] == currentValue) {
                int index = i;
                if(++index >= values.length)
                    index = 0;
                return values[index];
            }
        }
        return values[0];
    }
}
