package studio.archetype.shutter.client.processing;

public final class FfmpegProperties {

    public static CommandProperty FRAMERATE  = CommandProperty.property("-r", 60);
    public static CommandProperty QUALITY = CommandProperty.property("-crf", 25);
    public static CommandProperty RESOLUTION = CommandProperty.property("-s", "800x600");
    public static CommandProperty PIXEL_FORMAT = CommandProperty.property("-pix_fmt", "yuv420p");
    public static CommandProperty FORMAT = CommandProperty.property("-f", "image2");
    public static CommandProperty INPUT = CommandProperty.property("-i", "pipe:0");
    public static CommandProperty OUTPUT = CommandProperty.value("pipe:1");
    public static CommandProperty CODEC = CommandProperty.property("-vcodec", "libx264");
    public static CommandProperty OVERWRITE = CommandProperty.flag("-y");
}