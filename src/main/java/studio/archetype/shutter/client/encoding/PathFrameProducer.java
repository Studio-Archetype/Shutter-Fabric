package studio.archetype.shutter.client.encoding;

import com.github.kokorin.jaffree.ffmpeg.Frame;
import com.github.kokorin.jaffree.ffmpeg.FrameProducer;
import com.github.kokorin.jaffree.ffmpeg.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;

import java.util.Collections;
import java.util.List;

public class PathFrameProducer implements FrameProducer {

    private long timebase;

    public PathFrameProducer(long timebase) {
        this.timebase = timebase;
    }

    @Override
    public List<Stream> produceStreams() {
        return Collections.singletonList(new Stream()
                .setType(Stream.Type.VIDEO)
                .setId(0)
                .setTimebase(timebase)
                .setWidth(MinecraftClient.getInstance().getWindow().getWidth())
                .setHeight(MinecraftClient.getInstance().getWindow().getHeight())
            );
    }

    @Override
    public Frame produce() {
        return null;
    }
}