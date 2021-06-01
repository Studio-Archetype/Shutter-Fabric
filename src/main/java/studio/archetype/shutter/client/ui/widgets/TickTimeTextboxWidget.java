package studio.archetype.shutter.client.ui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.math.NumberUtils;
import studio.archetype.shutter.util.TimeUnits;

public class TickTimeTextboxWidget extends TextFieldWidget {

    private static final int ERROR_COLOUR = 16733525;
    private static final int VALID_COLOUR = 14737632;

    private final EnumButtonWidget<TimeUnits> unitButton;

    private float value;
    private TimeUnits currentUnit;
    private boolean isValid;

    public TickTimeTextboxWidget(int x, int y, int width, int height, float initialValue, TimeUnits initialUnit, Text name) {
        super(MinecraftClient.getInstance().textRenderer, x, y, width - (height + (height / 4)), height, name);
        this.currentUnit = initialUnit;

        setText(String.valueOf(initialValue));
        setMaxLength(32);
        setChangedListener(s -> this.isValid = parseValue(s));
        this.unitButton = new EnumButtonWidget<>(new LiteralText("Unit"), x + this.width + this.height / 4, y, height, height, TimeUnits.SECONDS, this::updateUnit);
        this.unitButton.setPrefix(false);
        this.isValid = parseValue(getText());
    }

    public boolean isValid() {
        return isValid;
    }

    public int getTicks() {
        return (int)TimeUnits.convert(this.value, this.currentUnit, TimeUnits.TICKS);
    }

    public AbstractButtonWidget getButton() {
        return unitButton;
    }

    public void updateUnit(TimeUnits unit) {
        this.currentUnit = unit;
        this.isValid = parseValue(getText());
    }

    @Override
    public void tick() {
        if(isValid)
            setEditableColor(VALID_COLOUR);
        else
            setEditableColor(ERROR_COLOUR);
        super.tick();
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
