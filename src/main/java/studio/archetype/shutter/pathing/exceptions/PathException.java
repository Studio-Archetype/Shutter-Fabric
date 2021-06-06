package studio.archetype.shutter.pathing.exceptions;

public class PathException extends Exception {

    public PathException() { }
    public PathException(String s, String... args) {
        super(String.format(s, (Object)args));
    }
}
