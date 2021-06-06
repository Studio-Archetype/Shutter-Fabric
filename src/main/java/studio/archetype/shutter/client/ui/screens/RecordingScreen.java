package studio.archetype.shutter.client.ui.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.config.FfmpegRecordConfig;
import studio.archetype.shutter.client.config.enums.RecordingMode;
import studio.archetype.shutter.client.processing.jobs.Jobs;
import studio.archetype.shutter.client.ui.Messaging;
import studio.archetype.shutter.client.ui.widgets.EnumButtonWidget;
import studio.archetype.shutter.client.ui.widgets.EnumSliderWidget;
import studio.archetype.shutter.client.ui.widgets.PredicateTextboxWidget;
import studio.archetype.shutter.client.ui.widgets.TickTimeTextboxWidget;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.exceptions.PathTooSmallException;
import studio.archetype.shutter.util.AsyncUtils;
import studio.archetype.shutter.util.TimeUnits;
import studio.archetype.shutter.util.cli.CliUtils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RecordingScreen extends Screen {

    private static final Text title = new TranslatableText("ui.shutter.recording.title");

    private final FfmpegRecordConfig config;

    private TickTimeTextboxWidget pathTime;
    private PredicateTextboxWidget filename;
    private ButtonWidget startButton;
    private CheckboxWidget skipCountdown;

    private int ticks;
    private String name;
    private CompletableFuture<Void> dummyFuture;

    public RecordingScreen(FfmpegRecordConfig config) {
        super(new LiteralText("shutter_recording"));
        this.config = config;
    }

    @Override
    protected void init() {
        this.filename = new PredicateTextboxWidget(width / 2 - 175, height / 2 - 57, 150, 20, new TranslatableText("ui.shutter.recording.filename"));
        filename.setTextFieldFocused(true);
        filename.setMaxLength(64);
        filename.setValidPredicate(s -> !s.contains(" ") && !s.isEmpty());
        addSelectableChild(this.filename);
        setInitialFocus(this.filename);
        this.pathTime = new TickTimeTextboxWidget(width / 2 + 25, height / 2 - 57, 150, 20, new TranslatableText("ui.shutter.recording.pathtime"), TimeUnits.convert(config.pathTimeTicks, TimeUnits.TICKS, TimeUnits.SECONDS), TimeUnits.SECONDS);
        addSelectableChild(pathTime);
        addDrawableChild(this.pathTime.getButton());

        addDrawableChild(new EnumButtonWidget<>(I18n.translate("ui.shutter.recording.codec"), width / 2 - 175, height / 2 - 34, 150, 20, config.codec, (v) -> config.codec = v));
        addDrawableChild(new EnumButtonWidget<>(I18n.translate("ui.shutter.recording.rendermode"), width / 2 + 25, height / 2 - 34, 150, 20, config.renderMode, (v) -> config.renderMode = v));

        addDrawableChild(new EnumSliderWidget<>(I18n.translate("ui.shutter.recording.framerate"), width / 2 - 175, height / 2 - 10, 150, 20, config.framerate, (v) -> config.framerate = v));
        addDrawableChild(new EnumSliderWidget<>(I18n.translate("ui.shutter.recording.preset"), width / 2 + 25, height / 2 - 10, 150, 20, config.preset, (v) -> config.preset = v));

        Text skipText = new TranslatableText("ui.shutter.recording.skip");
        int x = (20 + 4 + textRenderer.getWidth(skipText)) / 2;
        this.skipCountdown = addDrawableChild(new CheckboxWidget(width / 2 - x, height / 2 + 14, 20, 20, skipText, false));

        this.startButton = addDrawableChild(new ButtonWidget(width / 2 - 75, height / 2 + 38, 150, 20, new TranslatableText("ui.shutter.recording.start").formatted(Formatting.GREEN, Formatting.BOLD), (b) -> {
            this.config.pathTimeTicks = this.pathTime.getTicks();
            MinecraftClient.getInstance().currentScreen = null;
            this.name = this.filename.getText();
            if(this.config.renderMode == RecordingMode.VIDEO && !CliUtils.isCommandAvailable("ffmpeg")) {
                Messaging.sendMessage(
                        new TranslatableText("msg.shutter.headline.rec.failed"),
                        new TranslatableText("msg.shutter.error.no_ffmpeg"),
                        Messaging.MessageType.NEGATIVE);
                return;
            }
            if(this.skipCountdown.isChecked()) {
                this.onCountdownDone.accept(null);
            } else {
                this.ticks = 60;
                this.dummyFuture = new CompletableFuture<>();
                AsyncUtils.queueAsync(this.dummyFuture, this.onCountdownDone, this.onCountdownTick);
            }
        }));
    }

    @Override
    public void tick() {
        this.pathTime.tick();
        this.filename.tick();
        this.startButton.active = this.pathTime.isValid() && this.filename.isValid();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredText(matrices, textRenderer, title, this.width / 2, 25, Formatting.WHITE.getColorValue());
        this.pathTime.render(matrices, mouseX, mouseY, delta);
        this.filename.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private final Runnable onCountdownTick = () -> {
        MinecraftClient c = MinecraftClient.getInstance();
        if(ticks == 0) {
            c.inGameHud.clearTitle();
            dummyFuture.complete(null);
        } else {
            if(ticks == 60)
                displayCountdownTitle(c, 3);
            if(ticks == 40)
                displayCountdownTitle(c, 2);
            if(ticks == 20)
                displayCountdownTitle(c, 1);
            ticks--;
        }
    };

    private final Consumer<Void> onCountdownDone = (v) -> startRecording(MinecraftClient.getInstance().world, this.name);

    private void startRecording(World w, String name) {
        try {
            CameraPathManager manager = ShutterClient.INSTANCE.getPathManager(w);
            if(manager.isVisualizing())
                manager.togglePathVisualization(false);
            manager.startCameraPath(config.pathTimeTicks, false);
            Jobs.createNewJob(config.framerate.value, name);
        } catch(PathTooSmallException e) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.not_enough_start"),
                    Messaging.MessageType.NEGATIVE);
        }
    }

    private static void displayCountdownTitle(MinecraftClient c, int seconds) {

        MutableText title = new TranslatableText("ui.shutter.recording.countdown1", seconds).formatted(Formatting.BOLD, Formatting.GOLD);
        title.append(new LiteralText(String.valueOf(seconds)).formatted(Formatting.BOLD, switch (seconds) {
            case 3 -> Formatting.GREEN;
            case 2 -> Formatting.YELLOW;
            case 1 -> Formatting.RED;
            default -> Formatting.GRAY;
        })).append(new LiteralText("...").formatted(Formatting.BOLD, Formatting.GOLD));
        Text subtitle = new TranslatableText("ui.shutter.recording.countdown2").setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY));
        c.inGameHud.setTitle(title);
        c.inGameHud.setSubtitle(subtitle);
        c.inGameHud.setTitleTicks(-1, 19, -1);
    }
}
