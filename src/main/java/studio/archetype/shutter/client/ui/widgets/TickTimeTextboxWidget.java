package studio.archetype.shutter.client.ui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.math.NumberUtils;
import studio.archetype.shutter.util.TimeUnits;

import java.util.function.Predicate;

public class TickTimeTextboxWidget extends PredicateTextboxWidget {

    private final EnumButtonWidget<TimeUnits> unitButton;

    private float value;
    private TimeUnits currentUnit;

    public TickTimeTextboxWidget(int x, int y, int width, int height, Text label, float initialValue, TimeUnits initialUnit) {
        super(x, y, width - (height + (height / 4)), height, label);

        this.currentUnit = initialUnit;
        this.unitButton = new EnumButtonWidget<>("Unit", x + this.width + this.height / 4, y, height, height, TimeUnits.SECONDS, this::updateUnit);
        unitButton.setPrefix(false);

        setValidPredicate(this::parseValue);
        setText(String.valueOf(initialValue));
        setMaxLength(32);
    }

    public int getTicks() {
        return (int)TimeUnits.convert(this.value, this.currentUnit, TimeUnits.TICKS);
    }

    public ButtonWidget getButton() {
        return unitButton;
    }

    public void updateUnit(TimeUnits unit) {
        this.currentUnit = unit;
    }

    private boolean parseValue(String text) {
        if(NumberUtils.isParsable(text)) {
            if(currentUnit.isFloatingPoint) {
                try {
                    this.value = Float.parseFloat(text);
                    return true;
                } catch(NumberFormatException ex) {
                    return false;
                }
            } else {
                try {
                    this.value = Integer.parseInt(text);
                    return true;
                } catch(NumberFormatException ex) {
                    return false;
                }
            }
        } else
            return false;
    }
}
