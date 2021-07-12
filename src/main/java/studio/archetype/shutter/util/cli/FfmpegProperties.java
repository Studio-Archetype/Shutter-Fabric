package studio.archetype.shutter.util.cli;

import studio.archetype.shutter.client.config.enums.EncoderPreset;

public final class FfmpegProperties {

    public static CommandProperty FRAMERATE = CommandProperty.property("-r", 60);
    public static CommandProperty QUALITY = CommandProperty.property("-crf", 25);
    public static CommandProperty RESOLUTION = CommandProperty.property("-s", "800x600");
    public static CommandProperty PIXEL_FORMAT = CommandProperty.property("-pix_fmt", "yuv420p");
    public static CommandProperty CONTAINER = CommandProperty.property("-f", "image2");
    public static CommandProperty INPUT = CommandProperty.property("-i", "pipe:0");
    public static CommandProperty OUTPUT = CommandProperty.value("pipe:1");
    public static CommandProperty CODEC = CommandProperty.property("-vcodec", "libx264");
    public static CommandProperty OVERWRITE = CommandProperty.flag("-y");
    public static CommandProperty HIDE_BANNER = CommandProperty.flag("-hide_banner");
    public static CommandProperty PRESET = CommandProperty.property("-preset", EncoderPreset.MEDIUM.value);

    public static CommandProperty PROPS_X264 = CommandProperty.property("-x264-params", "");
    public static CommandProperty PROPS_X265 = CommandProperty.property("-x265-params", "");
}