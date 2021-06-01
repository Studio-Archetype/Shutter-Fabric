package studio.archetype.shutter.client.ui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import studio.archetype.shutter.client.config.FfmpegRecordConfig;
import studio.archetype.shutter.client.ui.widgets.EnumButtonWidget;
import studio.archetype.shutter.client.ui.widgets.EnumSliderWidget;
import studio.archetype.shutter.client.ui.widgets.TickTimeTextboxWidget;
import studio.archetype.shutter.util.TimeUnits;

public class RecordingScreen extends Screen {

    private final FfmpegRecordConfig config;

    private TickTimeTextboxWidget pathTime;
    private ButtonWidget startButton;

    public RecordingScreen(FfmpegRecordConfig config) {
        super(new LiteralText("shutter-recording"));
        this.config = config;
    }

    @Override
    protected void init() {
        addButton(new EnumButtonWidget<>(new LiteralText("Codec"), width / 2 - 175, height / 2 - 49, 150, 20, config.codec, (v) -> config.codec = v));
        addButton(new EnumButtonWidget<>(new LiteralText("Render Mode"), width / 2 + 25, height / 2 - 49, 150, 20, config.renderMode, (v) -> config.renderMode = v));

        addButton(new EnumSliderWidget<>(new LiteralText("Framerate"), width / 2 - 175, height / 2 - 10, 150, 20, config.framerate, (v) -> config.framerate = v));
        this.pathTime = new TickTimeTextboxWidget(width / 2 + 25, height / 2 - 10, 150, 20, config.pathTimeTicks, TimeUnits.SECONDS, new LiteralText("Path Time"));
        this.children.add(this.pathTime);
        addButton(this.pathTime.getButton());

        this.startButton = addButton(new ButtonWidget(width / 2 - 75, height / 2 + 29, 150, 20, new LiteralText("Start!").formatted(Formatting.GREEN, Formatting.BOLD), (b) -> {
            this.config.pathTimeTicks = this.pathTime.getTicks();
            //DO THE THING
        }));
    }

    @Override
    public void tick() {
        this.pathTime.tick();
        this.startButton.active = this.pathTime.isValid();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        this.pathTime.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
