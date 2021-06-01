package studio.archetype.shutter.util;

import java.util.function.Function;

public enum TimeUnits {
    TICKS(false, t -> (float)t, t -> (int)(t * 1), "T", "Ticks"),
    SECONDS(true, t -> t / 20F, t -> (int)(t * 20), "S", "Seconds"),
    MINUTES(true, t -> t / (20F * 60F), t -> (int)(t * 20 * 60), "M", "Minutes"),
    HOURS(true, t -> t / (20F * 60F * 60F), t -> (int)(t * 20 * 60 * 60), "H", "Hours");

    public final boolean isFloatingPoint;
    private final Function<Integer, Float> fromTicks;
    private final Function<Float, Integer> toTicks;
    private final String letter, fullUnit;

    TimeUnits(boolean isFloatingPoint, Function<Integer, Float> fromTicks, Function<Float, Integer> toTicks, String letter, String fullUnit) {
        this.isFloatingPoint = isFloatingPoint;
        this.fromTicks = fromTicks;
        this.toTicks = toTicks;
        this.letter = letter;
        this.fullUnit = fullUnit;
    }

    public static float convert(float value, TimeUnits from, TimeUnits to) {
        return to.fromTicks.apply(from.toTicks.apply(value));
    }

    @Override
    public String toString() {
        return letter;
    }
}