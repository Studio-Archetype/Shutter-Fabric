package studio.archetype.shutter.client.ui.widgets;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.LiteralText;

import java.util.function.Consumer;

public class EnumSliderWidget<E extends Enum<E>> extends SliderWidget {

    private final String label;
    private final E[] values;
    private final Consumer<E> action;
    private final double stepSize;
    private E currentValue;

    public EnumSliderWidget(String label, int x, int y, int width, int height, E startValue, Consumer<E> action) {
        super(x, y, width, height, new LiteralText("none"), 0);
        this.label = label;
        this.values = startValue.getDeclaringClass().getEnumConstants();
        this.action = action;
        this.stepSize = 1D / (values.length - 1);
        this.currentValue = startValue;
        this.value = getProgress(startValue);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        setMessage(new LiteralText(this.label + ": " + this.currentValue.toString()));
    }

    @Override
    protected void applyValue() {
        int index = (int) (this.value / this.stepSize);
        this.currentValue = values[index];
        this.value = getProgress(this.currentValue);
        updateMessage();
        this.action.accept(currentValue);
    }

    private double getProgress(E value) {
        for(int i = 0; i < values.length; i++)
            if(values[i] == value)
                return stepSize * i;
        return 0;
    }
}
