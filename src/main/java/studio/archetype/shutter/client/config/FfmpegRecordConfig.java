package studio.archetype.shutter.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.config.enums.EncoderPreset;
import studio.archetype.shutter.client.config.enums.RecordingCodec;
import studio.archetype.shutter.client.config.enums.RecordingFramerate;
import studio.archetype.shutter.client.config.enums.RecordingMode;

@Config(name = Shutter.MOD_ID + "_recording")
public class FfmpegRecordConfig implements ConfigData {

    public RecordingMode renderMode = RecordingMode.VIDEO;
    public RecordingFramerate framerate = RecordingFramerate.F60;
    public RecordingCodec codec = RecordingCodec.Hx265;
    public EncoderPreset preset = EncoderPreset.MEDIUM;
    public int pathTimeTicks = 200;
}
