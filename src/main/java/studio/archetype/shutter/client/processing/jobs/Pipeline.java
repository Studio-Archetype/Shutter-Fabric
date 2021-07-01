package studio.archetype.shutter.client.processing.jobs;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.processing.capturing.DummyCapturer;
import studio.archetype.shutter.client.processing.capturing.EnforcedFramerateCapturer;
import studio.archetype.shutter.client.processing.capturing.FrameCapturer;
import studio.archetype.shutter.client.processing.converting.DummyConverter;
import studio.archetype.shutter.client.processing.converting.FrameConverter;
import studio.archetype.shutter.client.processing.converting.OpenGl2BitmapConverter;
import studio.archetype.shutter.client.processing.frames.DummyFrame;
import studio.archetype.shutter.client.processing.frames.Frame;
import studio.archetype.shutter.client.processing.frames.OpenGlFrame;
import studio.archetype.shutter.client.processing.frames.RgbaFrame;
import studio.archetype.shutter.client.processing.processors.DummyProcessor;
import studio.archetype.shutter.client.processing.processors.FfmpegVideoProcessor;
import studio.archetype.shutter.client.processing.processors.FrameProcessor;
import studio.archetype.shutter.client.util.ScreenSize;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Pipeline<I extends Frame, O extends Frame, C extends FrameCapturer<I>, K extends FrameConverter<I, O>, P extends FrameProcessor<O>> implements Closeable {

    private final C frameCapturer;
    private final K frameConverter;
    private final P frameProcessor;

    private final Object processorLock = new Object();

    private int nextFrameId;
    private volatile boolean abort;

    private ExecutorService convertService;

    public Pipeline(C capture, K converter, P processor) {
        this.frameCapturer = capture;
        this.frameConverter = converter;
        this.frameProcessor = processor;
    }

    public void setup() {
        this.nextFrameId = 0;
        int availableThreads = Math.max(Runtime.getRuntime().availableProcessors() - 2, 1);
        this.convertService = new ThreadPoolExecutor(availableThreads, availableThreads, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(2) {
            public boolean offer(Runnable runnable) {
                try {
                    this.put(runnable);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return false;
                }
                return true;
            }
        }, new ThreadPoolExecutor.DiscardPolicy());
    }

    public boolean onRender() {
        MinecraftClient client = MinecraftClient.getInstance();
        if(!frameCapturer.isDone() && !abort) {
            if (GLFW.glfwWindowShouldClose(client.getWindow().getHandle())) {
                this.close();
                return true;
            }
            I inputFrame = this.frameCapturer.capture();
            if(inputFrame != null)
                convertService.submit(new ConvertTask(inputFrame));
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        this.convertService.shutdown();
        try {
            this.convertService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            if(ShutterClient.INSTANCE.getPathFollower().isFollowing())
                ShutterClient.INSTANCE.getPathFollower().end();
            frameCapturer.close();
            frameProcessor.close();
        } catch(IOException e) {
            //TODO Crash
        }
    }

    public void cancel() {
        abort = true;
    }

    private class ConvertTask implements Runnable {

        private final I inputFrame;

        public ConvertTask(I input) {
            this.inputFrame = input;
        }

        @Override
        public void run() {
            O outputFrame = frameConverter.convert(this.inputFrame);
            synchronized (processorLock) {
                while(nextFrameId != outputFrame.getFrameId()) {
                    try {
                        processorLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                try {
                    if(!abort)
                        frameProcessor.processFrame(outputFrame);
                } catch(IOException e) {
                    System.out.println("Unable to process Frame#" + outputFrame.getFrameId() + "!");
                    System.out.println("-> " + e.getMessage());
                    cancel();
                }
                nextFrameId++;
                processorLock.notifyAll();
            }
        }
    }

    public static Pipeline<OpenGlFrame, RgbaFrame, EnforcedFramerateCapturer, OpenGl2BitmapConverter, FfmpegVideoProcessor> getDefaultPipeline(int framerate, ScreenSize size, FfmpegVideoProcessor processor) {
        return new Pipeline<>(new EnforcedFramerateCapturer(framerate, size), new OpenGl2BitmapConverter(), processor);
    }

    public static Pipeline<DummyFrame, DummyFrame, DummyCapturer, DummyConverter, DummyProcessor> getDummyPipeline() {
        return new Pipeline<>(new DummyCapturer(), new DummyConverter(), new DummyProcessor());
    }
}
