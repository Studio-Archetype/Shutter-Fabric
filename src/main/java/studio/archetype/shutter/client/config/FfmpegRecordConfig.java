package studio.archetype.shutter.client.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.config.enums.RecordingCodec;
import studio.archetype.shutter.client.config.enums.RecordingFramerate;
import studio.archetype.shutter.client.config.enums.RecordingMode;

@Config(name = Shutter.MOD_ID)
public class FfmpegRecordConfig implements ConfigData {

    public RecordingMode renderMode = RecordingMode.VIDEO;

    public RecordingFramerate framerate = RecordingFramerate.F60;

    public RecordingCodec codec = RecordingCodec.Hx265;

    public boolean skipCountdown = false;
}
