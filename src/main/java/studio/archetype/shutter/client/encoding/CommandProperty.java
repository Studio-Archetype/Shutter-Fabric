package studio.archetype.shutter.client.encoding;

public class CommandProperty {

    private final String key;
    private final Object defaultValue;
    private Object value;

    public static CommandProperty flag(String flag) {
        return new CommandProperty(flag, null);
    }

    public static CommandProperty property(String key, Object defaultValue) {
        return new CommandProperty(key, defaultValue);
    }

    private CommandProperty(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public CommandProperty get(Object value) {
        CommandProperty c =  new CommandProperty(this.key, this.defaultValue);
        c.value = value;
        return c;
    }

    public CommandProperty get() {
        return get(this.defaultValue);
    }

    @Override
    public String toString() {
        if(defaultValue == null)
            return key;
        else
            return key + " " + value;
    }
}
