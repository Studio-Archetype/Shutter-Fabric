package studio.archetype.shutter.client.util;

public record ScreenSize(int width, int height) {

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
