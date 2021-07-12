package studio.archetype.shutter.util.cli;

public class CommandProperty {

    private final String key;
    private final Object value;

    public static CommandProperty flag(String flag) {
        return new CommandProperty(flag, null);
    }

    public static CommandProperty value(String value) {
        return new CommandProperty(null, value);
    }

    public static CommandProperty property(String key, Object defaultValue) {
        return new CommandProperty(key, defaultValue);
    }

    private CommandProperty(String key, Object defaultValue) {
        this.key = key;
        this.value = defaultValue;
    }

    public CommandProperty get(Object value) {
        return new CommandProperty(this.key, value);
    }

    @Override
    public String toString() {
        if(value == null)
            return key;
        else if(key == null)
            return value.toString();
        else
            return key + " " + value;
    }
}
